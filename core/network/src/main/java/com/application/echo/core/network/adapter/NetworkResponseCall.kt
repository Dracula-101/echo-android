package com.application.echo.core.network.adapter

import com.application.echo.core.network.model.Meta
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse
import com.application.echo.core.network.serialization.ResponseParser
import com.application.echo.core.network.util.toNetworkException
import com.google.gson.Gson
import java.lang.reflect.Type
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A [Call] wrapper that converts raw HTTP responses into [NetworkResponse].
 *
 * All paths — success, HTTP error, network failure, parse failure —
 * are funnelled into a `Response.success(NetworkResponse)`, so the
 * Retrofit callback **never** receives `onFailure`.
 */
internal class NetworkResponseCall<T>(
    private val delegate: Call<ResponseBody>,
    private val gson: Gson,
    private val successType: Type,
) : Call<NetworkResponse<T>> {

    // ──────────────── Async ────────────────

    override fun enqueue(callback: Callback<NetworkResponse<T>>) {
        delegate.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {
                val networkResponse = ResponseParser.parse<T>(response, successType, gson)
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(networkResponse),
                )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val networkResponse = NetworkResponse.Error(
                    meta = Meta(
                        success = false,
                        statusCode = -1,
                        timestamp = System.currentTimeMillis().toString(),
                    ),
                    error = t.toNetworkException(),
                )
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(networkResponse),
                )
            }
        })
    }

    // ──────────────── Sync ────────────────

    override fun execute(): Response<NetworkResponse<T>> {
        return try {
            val rawResponse = delegate.execute()
            val networkResponse = ResponseParser.parse<T>(rawResponse, successType, gson)
            Response.success(networkResponse)
        } catch (e: Exception) {
            val networkResponse = NetworkResponse.Error(
                meta = Meta(
                    success = false,
                    statusCode = -1,
                    timestamp = System.currentTimeMillis().toString(),
                ),
                error = e.toNetworkException(),
            )
            Response.success(networkResponse)
        }
    }

    // ──────────────── Delegation ────────────────

    override fun clone(): Call<NetworkResponse<T>> =
        NetworkResponseCall(delegate.clone(), gson, successType)

    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun cancel() = delegate.cancel()
}
