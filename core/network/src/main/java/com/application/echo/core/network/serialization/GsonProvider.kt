package com.application.echo.core.network.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Centralized [Gson] instance factory.
 *
 * Every part of the network module that needs JSON (de)serialization
 * should use the instance created here so date formats and policies are
 * consistent.
 */
object GsonProvider {

    /** ISO-8601 date format expected by the Echo API. */
    private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /**
     * Creates a production [Gson] instance.
     */
    fun create(): Gson = GsonBuilder()
        .setDateFormat(DATE_FORMAT)
        .serializeNulls()
        .create()

    /**
     * Creates a debug [Gson] instance with pretty-printing.
     */
    fun createDebug(): Gson = GsonBuilder()
        .setDateFormat(DATE_FORMAT)
        .serializeNulls()
        .setPrettyPrinting()
        .create()
}
