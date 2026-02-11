package com.application.echo.core.api.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

/**
 * Helper for converting Android [Uri] to [File] for media uploads.
 *
 * ```kotlin
 * val (file, mimeType) = MediaHelper.prepareUpload(context, imageUri)
 * mediaRepo.uploadProfilePhoto(file, mimeType)
 * ```
 */
object MediaHelper {

    /**
     * Copies the content at [uri] into a temporary [File] in the app's cache directory.
     *
     * @return a [PreparedFile] with the temp file and its MIME type.
     * @throws IllegalArgumentException if the URI cannot be opened.
     */
    fun prepareUpload(context: Context, uri: Uri): PreparedFile {
        val contentResolver = context.contentResolver

        val mimeType = contentResolver.getType(uri) ?: FALLBACK_MIME_TYPE
        val fileName = getFileName(contentResolver, uri) ?: generateFileName(mimeType)

        val tempFile = File(context.cacheDir, "$UPLOAD_DIR/$fileName").apply {
            parentFile?.mkdirs()
        }

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalArgumentException("Unable to open URI: $uri")

        return PreparedFile(
            file = tempFile,
            mimeType = mimeType,
            originalFileName = fileName,
        )
    }

    /**
     * Resolves the MIME type for a [Uri], falling back to `application/octet-stream`.
     */
    fun getMimeType(context: Context, uri: Uri): String =
        context.contentResolver.getType(uri) ?: FALLBACK_MIME_TYPE

    /**
     * Cleans up temporary upload files from the cache directory.
     */
    fun cleanupTempFiles(context: Context) {
        val uploadDir = File(context.cacheDir, UPLOAD_DIR)
        if (uploadDir.exists()) {
            uploadDir.deleteRecursively()
        }
    }

    // ──────────────── Internal ────────────────

    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    return cursor.getString(nameIndex)
                }
            }
        }
        return uri.lastPathSegment
    }

    private fun generateFileName(mimeType: String): String {
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "bin"
        return "upload_${System.currentTimeMillis()}.$extension"
    }

    private const val UPLOAD_DIR = "echo_uploads"
    private const val FALLBACK_MIME_TYPE = "application/octet-stream"
}

/**
 * Result of [MediaHelper.prepareUpload].
 *
 * @property file Temporary file in the app cache (ready for upload).
 * @property mimeType Resolved MIME type of the file.
 * @property originalFileName The original file name from the content provider.
 */
data class PreparedFile(
    val file: File,
    val mimeType: String,
    val originalFileName: String,
)
