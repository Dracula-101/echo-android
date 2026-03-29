package com.application.echo.core.common.platform.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// Uri → File
// ─────────────────────────────────────────────────────────────

fun Uri.toFile(context: Context): File {
    if (scheme == "file") return File(requireNotNull(path))

    val extension = context.contentResolver
        .getType(this)
        ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
        ?: "tmp"

    val tempFile = File.createTempFile("upload_", ".$extension", context.cacheDir)

    context.contentResolver.openInputStream(this)?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    } ?: throw IOException("Cannot open stream for URI: $this")

    return tempFile
}

// ─────────────────────────────────────────────────────────────
// Metadata
// ─────────────────────────────────────────────────────────────

fun Uri.getMimeType(context: Context): String? =
    context.contentResolver.getType(this)

fun Uri.getExtension(context: Context): String? =
    getMimeType(context)
        ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
        ?: path?.substringAfterLast('.', "")?.takeIf { it.isNotBlank() }

fun Uri.getFileName(context: Context): String? {
    if (scheme == "file") return File(requireNotNull(path)).name
    return context.contentResolver.query(
        this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
    )?.use { cursor ->
        if (cursor.moveToFirst())
            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        else null
    }
}

fun Uri.getFileSize(context: Context): Long? {
    if (scheme == "file") return File(requireNotNull(path)).length()
    return context.contentResolver.query(
        this, arrayOf(OpenableColumns.SIZE), null, null, null
    )?.use { cursor ->
        if (cursor.moveToFirst())
            cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
        else null
    }
}

// ─────────────────────────────────────────────────────────────
// Type checks
// ─────────────────────────────────────────────────────────────

fun Uri.isImage(context: Context): Boolean =
    getMimeType(context)?.startsWith("image/") == true

fun Uri.isVideo(context: Context): Boolean =
    getMimeType(context)?.startsWith("video/") == true

fun Uri.isAudio(context: Context): Boolean =
    getMimeType(context)?.startsWith("audio/") == true

fun Uri.isPdf(context: Context): Boolean =
    getMimeType(context) == "application/pdf"

// ─────────────────────────────────────────────────────────────
// Read bytes / string
// ─────────────────────────────────────────────────────────────

fun Uri.readBytes(context: Context): ByteArray =
    context.contentResolver.openInputStream(this)?.use { it.readBytes() }
        ?: throw IOException("Cannot open stream for URI: $this")

// ─────────────────────────────────────────────────────────────
// Existence / validity
// ─────────────────────────────────────────────────────────────

fun Uri.exists(context: Context): Boolean = try {
    context.contentResolver.openInputStream(this)?.use { true } ?: false
} catch (e: Exception) {
    false
}