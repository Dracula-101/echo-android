package com.application.echo.core.common.platform.base

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Base class for persistent disk storage that disallows clearing or removing values.
 */
@Suppress("UnnecessaryAbstractClass", "TooManyFunctions")
abstract class BasePersistentDiskSource(
    private val sharedPreferences: SharedPreferences,
) {
    protected fun getBoolean(key: String): Boolean? =
        if (sharedPreferences.contains(key.withBase())) {
            sharedPreferences.getBoolean(key.withBase(), false)
        } else {
            null
        }

    protected fun putBoolean(key: String, value: Boolean?) {
        if (value != null) {
            sharedPreferences.edit { putBoolean(key.withBase(), value) }
        }
    }

    protected fun getInt(key: String): Int? =
        if (sharedPreferences.contains(key.withBase())) {
            sharedPreferences.getInt(key.withBase(), 0)
        } else {
            null
        }

    protected fun putInt(key: String, value: Int?) {
        if (value != null) {
            sharedPreferences.edit { putInt(key.withBase(), value) }
        }
    }

    protected fun getLong(key: String): Long? =
        if (sharedPreferences.contains(key.withBase())) {
            sharedPreferences.getLong(key.withBase(), 0)
        } else {
            null
        }

    protected fun putLong(key: String, value: Long?) {
        if (value != null) {
            sharedPreferences.edit { putLong(key.withBase(), value) }
        }
    }

    protected fun getString(key: String): String? =
        sharedPreferences.getString(key.withBase(), null)

    protected fun putString(key: String, value: String?) {
        if (value != null) {
            sharedPreferences.edit { putString(key.withBase(), value) }
        }
    }

    protected fun getStringSet(key: String, default: Set<String>?): Set<String>? =
        sharedPreferences.getStringSet(key.withBase(), default)

    protected fun putStringSet(key: String, value: Set<String>?) {
        if (value != null) {
            sharedPreferences.edit { putStringSet(key.withBase(), value) }
        }
    }

    protected fun String.appendIdentifier(identifier: String): String = "${this}_$identifier"
}

/**
 * Helper method for prepending the key with the appropriate base storage key.
 */
private fun String.withBase(): String = "EchoPrefKey:$this"
