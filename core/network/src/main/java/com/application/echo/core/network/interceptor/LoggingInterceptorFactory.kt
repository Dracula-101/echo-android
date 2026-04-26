package com.application.echo.core.network.interceptor

import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

internal object LoggingInterceptorFactory {

    private const val TAG = "EchoHttp"
    private const val MAX_BODY_LENGTH = 10_000
    private const val CHUNK_SIZE = 4_000

    fun create(logBody: Boolean): Interceptor = Interceptor { chain ->
        val request = chain.request()
        val isMultipart = request.body is MultipartBody

        if (!logBody) {
            return@Interceptor buildOkHttpLogger(HttpLoggingInterceptor.Level.BASIC).intercept(chain)
        }

        logRequest(request, isMultipart)

        val startMs = System.currentTimeMillis()
        val response: Response = chain.proceed(request)
        val elapsedMs = System.currentTimeMillis() - startMs

        logResponse(response, request, elapsedMs)

        response
    }

    private fun logRequest(request: Request, isMultipart: Boolean) {
        log("┌── REQUEST  ${request.method} ${request.url}")
        request.headers.forEach { (name, value) ->
            val display = if (name.equals(HeaderConstants.AUTHORIZATION, ignoreCase = true)) "<redacted>" else value
            log("│ $name: $display")
        }
        if (isMultipart) {
            log("│ [multipart body omitted]")
        } else {
            request.peekBodyString()?.let { logBody(it) }
        }
        log("└${divider()}")
    }

    private fun logResponse(response: Response, request: Request, elapsedMs: Long) {
        log("┌── RESPONSE ${response.code} ${response.message} (${elapsedMs}ms)")
        log("│ ${request.url}")
        response.headers.forEach { (name, value) -> log("│ $name: $value") }
        response.peekBody(Long.MAX_VALUE)?.string()
            ?.takeIf { it.isNotBlank() }
            ?.let { logBody(it) }
        log("└${divider()}")
    }

    private fun logBody(raw: String) {
        val pretty = prettyPrintJson(raw)
        val output = if (pretty.length > MAX_BODY_LENGTH) {
            pretty.take(MAX_BODY_LENGTH) + "\n│ ... [truncated — ${pretty.length} chars total]"
        } else {
            pretty
        }
        log("│")
        output.lines().forEach { log("│   $it") }
    }

    private fun prettyPrintJson(raw: String): String {
        val trimmed = raw.trim()
        return runCatching {
            when {
                trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
                trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
                else -> trimmed
            }
        }.getOrDefault(trimmed).replace("\\/", "/")
    }

    private fun Request.peekBodyString(): String? = runCatching {
        val buffer = Buffer()
        newBuilder().build().body?.writeTo(buffer)
        buffer.readUtf8()
    }.getOrNull()?.takeIf { it.isNotBlank() }

    private fun buildOkHttpLogger(level: HttpLoggingInterceptor.Level) =
        HttpLoggingInterceptor { message ->
            message.chunked(CHUNK_SIZE).forEach { Timber.tag(TAG).d(it) }
        }.apply {
            redactHeader(HeaderConstants.AUTHORIZATION)
            this.level = level
        }

    private fun log(message: String) =
        message.chunked(CHUNK_SIZE).forEach { Timber.tag(TAG).d(it) }

    private fun divider() = "─".repeat(40)
}