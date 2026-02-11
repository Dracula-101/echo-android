package com.application.echo.core.network.model

/**
 * Type-safe HTTP status codes with semantic grouping.
 *
 * Eliminates magic numbers and enables pattern-matching on status ranges.
 */
@JvmInline
value class HttpStatusCode(val code: Int) {

    val isSuccess: Boolean get() = code in 200..299
    val isRedirect: Boolean get() = code in 300..399
    val isClientError: Boolean get() = code in 400..499
    val isServerError: Boolean get() = code in 500..599

    val isUnauthorized: Boolean get() = code == UNAUTHORIZED.code
    val isForbidden: Boolean get() = code == FORBIDDEN.code
    val isNotFound: Boolean get() = code == NOT_FOUND.code
    val isValidationError: Boolean get() = code == UNPROCESSABLE_ENTITY.code
    val isTooManyRequests: Boolean get() = code == TOO_MANY_REQUESTS.code

    override fun toString(): String = "HTTP $code"

    companion object {
        // 2xx
        val OK = HttpStatusCode(200)
        val CREATED = HttpStatusCode(201)
        val ACCEPTED = HttpStatusCode(202)
        val NO_CONTENT = HttpStatusCode(204)

        // 3xx
        val MOVED_PERMANENTLY = HttpStatusCode(301)
        val NOT_MODIFIED = HttpStatusCode(304)

        // 4xx
        val BAD_REQUEST = HttpStatusCode(400)
        val UNAUTHORIZED = HttpStatusCode(401)
        val FORBIDDEN = HttpStatusCode(403)
        val NOT_FOUND = HttpStatusCode(404)
        val METHOD_NOT_ALLOWED = HttpStatusCode(405)
        val CONFLICT = HttpStatusCode(409)
        val UNPROCESSABLE_ENTITY = HttpStatusCode(422)
        val TOO_MANY_REQUESTS = HttpStatusCode(429)

        // 5xx
        val INTERNAL_SERVER_ERROR = HttpStatusCode(500)
        val BAD_GATEWAY = HttpStatusCode(502)
        val SERVICE_UNAVAILABLE = HttpStatusCode(503)
        val GATEWAY_TIMEOUT = HttpStatusCode(504)

        // Special
        val UNKNOWN = HttpStatusCode(-1)
    }
}
