package com.application.echo.core.network.adapter

import com.application.echo.core.network.model.NetworkResponse
import com.google.gson.Gson
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter

/**
 * Adapts a raw `Call<ResponseBody>` into `Call<NetworkResponse<T>>`.
 *
 * Tells Retrofit to fetch the raw [ResponseBody] and delegates
 * parsing to [NetworkResponseCall].
 */
internal class NetworkResponseCallAdapter<T>(
    private val successType: Type,
    private val gson: Gson,
) : CallAdapter<ResponseBody, Call<NetworkResponse<T>>> {

    /**
     * Tells Retrofit to deserialize the HTTP body as [ResponseBody] (raw bytes).
     * Actual parsing happens in [NetworkResponseCall].
     */
    override fun responseType(): Type = ResponseBody::class.java

    override fun adapt(call: Call<ResponseBody>): Call<NetworkResponse<T>> {
        return NetworkResponseCall(
            delegate = call,
            gson = gson,
            successType = successType,
        )
    }
}
