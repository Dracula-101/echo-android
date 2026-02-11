package com.application.echo.core.network.adapter

import com.application.echo.core.network.model.NetworkResponse
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

/**
 * Retrofit [CallAdapter.Factory] that intercepts return types of the form
 * `Call<NetworkResponse<T>>` and wraps them with [NetworkResponseCallAdapter].
 *
 * This is the entry-point of the custom adapter chain:
 * ```
 * Factory  →  CallAdapter  →  NetworkResponseCall
 *                                  ↓
 *                           ResponseParser.parse()
 * ```
 *
 * Register it on a Retrofit.Builder:
 * ```
 * Retrofit.Builder()
 *     .addCallAdapterFactory(NetworkResponseCallAdapterFactory(gson))
 * ```
 */
class NetworkResponseCallAdapterFactory(
    private val gson: Gson,
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        // Must be Call<...>
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "$returnType must be parameterized" }

        // The inner type must be NetworkResponse<...>
        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != NetworkResponse::class.java) return null
        check(responseType is ParameterizedType) { "$responseType must be parameterized" }

        // Extract T from NetworkResponse<T>
        val dataType = getParameterUpperBound(0, responseType)

        return NetworkResponseCallAdapter<Any>(successType = dataType, gson = gson)
    }
}
