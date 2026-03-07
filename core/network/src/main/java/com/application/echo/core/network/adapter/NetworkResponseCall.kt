package com.application.echo.core.network.adapter

import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse
import com.application.echo.core.network.serialization.ResponseParser
import com.google.gson.Gson
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class NetworkResponseCall<T>(
    private val delegate: Call<ResponseBody>,
    private val gson: Gson,
    private val successType: Type,
) : Call<NetworkResponse<T>> {

    override fun enqueue(callback: Callback<NetworkResponse<T>>) {
        delegate.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(response.toNetworkResponse()),
                )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(t.toNetworkResponse()),
                )
            }
        })
    }

    override fun execute(): Response<NetworkResponse<T>> {
        val networkResponse = try {
            delegate.execute().toNetworkResponse()
        } catch (t: Throwable) {
            t.toNetworkResponse()
        }
        return Response.success(networkResponse)
    }

    private fun Response<ResponseBody>.toNetworkResponse(): NetworkResponse<T> {
        val rawBody = body() ?: errorBody()

        if (rawBody == null) {
            return NetworkResponse.Error(
                meta = null,
                error = NetworkException.Unknown(
                    throwable = IllegalStateException(
                        "Response body was null with no error body (HTTP ${code()})"
                    ),
                ),
            )
        }

        return runCatching {
            rawBody.use { body ->
                ResponseParser.parse<T>(
                    body = body,
                    rawResponse = this,
                    successType = successType,
                    gson = gson,
                )
            }
        }.getOrElse { t ->
            t.toNetworkResponse()
        }
    }

    private fun Throwable.toNetworkResponse(): NetworkResponse.Error =
        NetworkResponse.Error(meta = null, error = toNetworkException())

    private fun Throwable.toNetworkException(): NetworkException = when (this) {
        is SocketTimeoutException -> NetworkException.Timeout(throwable = this)
        is IOException -> NetworkException.Network(throwable = this)
        else -> NetworkException.Unknown(throwable = this)
    }

    override fun clone(): Call<NetworkResponse<T>> =
        NetworkResponseCall(delegate.clone(), gson, successType)

    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun cancel() = delegate.cancel()
}