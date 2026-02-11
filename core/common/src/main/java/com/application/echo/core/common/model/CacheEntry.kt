package com.application.echo.core.common.model

/**
 * Cache entry with expiration support
 */
data class CacheEntry<V>(
    val value: V,
    val timestamp: Long,
    val ttl: Long? = null // Time to live in milliseconds
) {
    fun isExpired(): Boolean {
        return ttl?.let { System.currentTimeMillis() - timestamp > it } ?: false
    }
}