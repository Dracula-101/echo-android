package com.application.echo.core.network.serialization

import com.application.echo.core.network.model.ApiEnvelope
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

internal object ResponseParser {

    fun <T> parse(
        body: ResponseBody,
        rawResponse: Response<*>,
        successType: Type,
        gson: Gson,
    ): NetworkResponse<T> {
        val raw = try {
            body.string()
        } catch (e: IOException) {
            return NetworkResponse.Error(
                meta = null,
                error = NetworkException.Serialization(
                    throwable = e,
                    body = null,
                ),
            )
        }

        val envelope: ApiEnvelope<T>? = try {
            val envelopeType = TypeToken.getParameterized(ApiEnvelope::class.java, successType).type
            gson.fromJson(raw, envelopeType)
        } catch (e: JsonParseException) {
            return NetworkResponse.Error(
                meta = null,
                error = NetworkException.Serialization(
                    throwable = e,
                    body = raw,
                ),
            )
        } catch (e: Throwable) {
            return NetworkResponse.Error(
                meta = null,
                error = NetworkException.Unknown(throwable = e),
            )
        }

        if (envelope == null) {
            return NetworkResponse.Error(
                meta = null,
                error = NetworkException.Serialization(
                    throwable = IllegalStateException("Server returned a null envelope"),
                    body = raw,
                ),
            )
        }

        return if (envelope.success) {
            val data = envelope.data
            val meta = envelope.metadata

            if (data == null || meta == null) {
                NetworkResponse.Error(
                    meta = null,
                    error = NetworkException.Serialization(
                        throwable = IllegalStateException(
                            "Successful envelope is missing required fields " +
                                    "[data=${data == null}, metadata=${meta == null}]"
                        ),
                        body = raw,
                    ),
                )
            } else {
                NetworkResponse.Success(
                    data = data,
                    message = envelope.message,
                    meta = meta,
                )
            }
        } else {
            NetworkResponse.Error(
                meta = envelope.metadata,
                error = NetworkException.Http(
                    throwable = HttpException(rawResponse),
                    message = envelope.message,
                    error = envelope.error,
                ),
            )
        }
    }
}