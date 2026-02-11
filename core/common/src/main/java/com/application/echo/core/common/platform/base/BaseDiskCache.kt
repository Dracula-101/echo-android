package com.application.echo.core.common.platform.base

import android.content.SharedPreferences
import androidx.core.content.edit
import com.application.echo.core.common.model.DiskCacheEntry

/**
 * Base class for persistent disk storage with type safety and expiration
 */
abstract class BaseDiskCache(
    private val sharedPreferences: SharedPreferences,
    private val keyPrefix: String = "cache"
) {
    private companion object {
        const val TTL_SUFFIX = "_ttl"
    }

    protected fun getBoolean(key: String): Boolean? {
        if (!sharedPreferences.contains(key.withPrefix())) return null

        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return null
        }

        return sharedPreferences.getBoolean(key.withPrefix(), false)
    }

    protected fun putBoolean(key: String, value: Boolean?, ttl: Long? = null) {
        if (value == null) return

        sharedPreferences.edit {
            putBoolean(key.withPrefix(), value)
            putTtlEntry(key, ttl)
        }
    }

    protected fun getInt(key: String): Int? {
        if (!sharedPreferences.contains(key.withPrefix())) return null

        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return null
        }

        return sharedPreferences.getInt(key.withPrefix(), 0)
    }

    protected fun putInt(key: String, value: Int?, ttl: Long? = null) {
        if (value == null) return

        sharedPreferences.edit {
            putInt(key.withPrefix(), value)
            putTtlEntry(key, ttl)
        }
    }

    protected fun getLong(key: String): Long? {
        if (!sharedPreferences.contains(key.withPrefix())) return null

        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return null
        }

        return sharedPreferences.getLong(key.withPrefix(), 0L)
    }

    protected fun putLong(key: String, value: Long?, ttl: Long? = null) {
        if (value == null) return

        sharedPreferences.edit {
            putLong(key.withPrefix(), value)
            putTtlEntry(key, ttl)
        }
    }

    protected fun getString(key: String): String? {
        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return null
        }

        return sharedPreferences.getString(key.withPrefix(), null)
    }

    protected fun putString(key: String, value: String?, ttl: Long? = null) {
        if (value == null) return

        sharedPreferences.edit {
            putString(key.withPrefix(), value)
            putTtlEntry(key, ttl)
        }
    }

    protected fun getStringSet(key: String): Set<String>? {
        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return null
        }

        return sharedPreferences.getStringSet(key.withPrefix(), null)
    }

    protected fun putStringSet(key: String, value: Set<String>?, ttl: Long? = null) {
        if (value == null) return

        sharedPreferences.edit {
            putStringSet(key.withPrefix(), value)
            putTtlEntry(key, ttl)
        }
    }

    protected fun remove(key: String): Boolean {
        return sharedPreferences.edit {
            remove(key.withPrefix())
            remove(key.withTtlSuffix())
        }.let { true }
    }

    protected fun clear() {
        val keysToRemove = sharedPreferences.all.keys.filter {
            it.startsWith("$keyPrefix:")
        }

        if (keysToRemove.isNotEmpty()) {
            sharedPreferences.edit {
                keysToRemove.forEach { key -> remove(key) }
            }
        }
    }

    protected fun size(): Int {
        return sharedPreferences.all.keys.count {
            it.startsWith("$keyPrefix:") && !it.endsWith(TTL_SUFFIX)
        }
    }

    protected fun containsKey(key: String): Boolean {
        if (!sharedPreferences.contains(key.withPrefix())) return false

        val ttlEntry = getTtlEntry(key)
        if (ttlEntry?.isExpired() == true) {
            removeWithTtl(key)
            return false
        }

        return true
    }

    protected fun getAllKeys(): Set<String> {
        return sharedPreferences.all.keys
            .filter { it.startsWith("$keyPrefix:") && !it.endsWith(TTL_SUFFIX) }
            .map { it.removePrefix("$keyPrefix:") }
            .filter { key ->
                val ttlEntry = getTtlEntry(key)
                if (ttlEntry?.isExpired() == true) {
                    removeWithTtl(key)
                    false
                } else {
                    true
                }
            }
            .toSet()
    }

    private fun getTtlEntry(key: String): DiskCacheEntry? {
        val ttlData = sharedPreferences.getString(key.withTtlSuffix(), null) ?: return null
        return DiskCacheEntry.decode(ttlData)
    }

    private fun SharedPreferences.Editor.putTtlEntry(key: String, ttl: Long?) {
        if (ttl != null) {
            val encoded = DiskCacheEntry.encode(System.currentTimeMillis(), ttl)
            putString(key.withTtlSuffix(), encoded)
        } else {
            remove(key.withTtlSuffix())
        }
    }

    private fun removeWithTtl(key: String) {
        sharedPreferences.edit {
            remove(key.withPrefix())
            remove(key.withTtlSuffix())
        }
    }

    protected fun String.appendIdentifier(identifier: String): String = "${this}_$identifier"

    private fun String.withPrefix(): String = "$keyPrefix:$this"
    private fun String.withTtlSuffix(): String = "$keyPrefix:$this$TTL_SUFFIX"
}