package com.application.echo.core.common.model

/**
 * Disk cache entry with TTL support
 */
data class DiskCacheEntry(
    val timestamp: Long,
    val ttl: Long? = null
) {
    fun isExpired(): Boolean {
        return ttl?.let { System.currentTimeMillis() - timestamp > it } ?: false
    }

    companion object {
        private const val SEPARATOR = "\u001F" // ASCII Unit Separator (rare in normal text)
        private const val NULL_MARKER = "\u001E" // ASCII Record Separator for null values

        fun encode(timestamp: Long, ttl: Long?): String {
            val ttlValue = ttl?.toString() ?: NULL_MARKER
            return "$timestamp$SEPARATOR$ttlValue"
        }

        fun decode(encoded: String): DiskCacheEntry? {
            return try {
                val separatorIndex = encoded.indexOf(SEPARATOR)
                if (separatorIndex == -1) return null

                val timestampStr = encoded.substring(0, separatorIndex)
                val ttlStr = encoded.substring(separatorIndex + 1)

                val timestamp = timestampStr.toLong()
                val ttl = if (ttlStr == NULL_MARKER) null else ttlStr.toLong()

                DiskCacheEntry(timestamp, ttl)
            } catch (e: Exception) {
                null
            }
        }
    }
}