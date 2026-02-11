package com.application.echo.core.network.serialization

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.Meta
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

/**
 * Parses raw OkHttp [Response] bodies into [NetworkResponse] instances.
 *
 * The Echo API returns a fixed envelope:
 * ```json
 * {
 *   "meta": { "success": true, "statusCode": 200, "timestamp": "..." },
 *   "message": "OK",
 *   "data": { ... }
 * }
 * ```
 * On error the `data` key is replaced by `errors`:
 * ```json
 * {
 *   "meta": { "success": false, "statusCode": 422, "timestamp": "..." },
 *   "message": "Validation failed",
 *   "errors": [ { "code": "...", "field": "email", "message": "..." } ]
 * }
 * ```
 */
internal object ResponseParser {

    /**
     * Parses an OkHttp [Response] into a typed [NetworkResponse].
     *
     * @param response The raw HTTP response.
     * @param successType The concrete type of `T` in `NetworkResponse<T>`.
     * @param gson The [Gson] instance to use.
     */
    fun <T> parse(
        response: Response<okhttp3.ResponseBody>,
        successType: Type,
        gson: Gson,
    ): NetworkResponse<T> {
        val rawBody = try {
            response.body()?.string() ?: response.errorBody()?.string() ?: ""
        } catch (e: Exception) {
            Timber.w(e, "Failed to read response body")
            ""
        }

        if (rawBody.isEmpty()) {
            return emptyResponseError(response)
        }

        return try {
            val jsonObject = gson.fromJson(rawBody, com.google.gson.JsonObject::class.java)
            val metaJson = jsonObject.getAsJsonObject("meta")
            val isSuccess = metaJson?.get("success")?.asBoolean ?: false

            if (isSuccess) {
                parseSuccess(rawBody, successType, gson)
            } else {
                parseError(rawBody, response, gson)
            }
        } catch (e: JsonSyntaxException) {
            Timber.e(e, "JSON syntax error while parsing response")
            NetworkResponse.Error(
                meta = fallbackMeta(response.code()),
                error = NetworkException.Serialization(throwable = e, body = rawBody),
            )
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error parsing response")
            NetworkResponse.Error(
                meta = fallbackMeta(response.code()),
                error = NetworkException.Unknown(e),
            )
        }
    }

    // ──────────────── Success Path ────────────────

    @Suppress("UNCHECKED_CAST")
    private fun <T> parseSuccess(
        rawBody: String,
        successType: Type,
        gson: Gson,
    ): NetworkResponse<T> {
        val envelopeType = createParameterizedType(SuccessEnvelope::class.java, successType)
        val envelope = gson.fromJson<SuccessEnvelope<T>>(rawBody, envelopeType)
        return NetworkResponse.Success(
            meta = envelope.meta,
            message = envelope.message,
            data = envelope.data,
        )
    }

    // ──────────────── Error Path ────────────────

    private fun <T> parseError(
        rawBody: String,
        response: Response<okhttp3.ResponseBody>,
        gson: Gson,
    ): NetworkResponse<T> {
        val envelope = gson.fromJson(rawBody, ErrorEnvelope::class.java)
        return NetworkResponse.Error(
            meta = envelope.meta,
            error = NetworkException.Http(
                throwable = HttpException(response),
                message = envelope.message,
                errors = envelope.errors,
            ),
        )
    }

    // ──────────────── Helpers ────────────────

    private fun <T> emptyResponseError(
        response: Response<okhttp3.ResponseBody>,
    ): NetworkResponse<T> = NetworkResponse.Error(
        meta = fallbackMeta(response.code()),
        error = NetworkException.Http(
            throwable = HttpException(response),
            message = "Empty response body",
            errors = null,
        ),
    )

    private fun fallbackMeta(statusCode: Int) = Meta(
        success = false,
        statusCode = statusCode,
        timestamp = System.currentTimeMillis().toString(),
    )

    private fun createParameterizedType(
        rawType: Type,
        vararg typeArguments: Type,
    ): ParameterizedType = object : ParameterizedType {
        override fun getActualTypeArguments(): Array<out Type> = typeArguments
        override fun getRawType(): Type = rawType
        override fun getOwnerType(): Type? = null
    }

    // ──────────────── Internal Envelope DTOs ────────────────

    internal data class SuccessEnvelope<T>(
        val meta: Meta,
        val message: String,
        val data: T,
    )

    internal data class ErrorEnvelope(
        val meta: Meta,
        val message: String,
        val errors: List<ApiError>? = null,
    )
}
