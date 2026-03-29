package com.application.echo.core.common.platform.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.net.URLConnection
import androidx.core.net.toUri

/**
 * A comprehensive utility class for resolving MIME types across various input sources.
 *
 * Supports resolution from:
 *  - File paths and File objects
 *  - URIs (content://, file://, http://)
 *  - File extensions
 *  - Raw byte arrays (magic bytes / file signatures)
 *  - URL strings
 *
 * Usage:
 *   val resolver = MimeTypeResolver(context)
 *   val mime = resolver.fromFile(file)
 *   val mime = resolver.fromUri(uri)
 *   val mime = resolver.fromExtension("pdf")
 *   val mime = resolver.fromBytes(byteArray)
 */
class MimeTypeResolver(private val context: Context) {

    // ─────────────────────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────────────────────

    companion object {
        const val MIME_TYPE_UNKNOWN        = "application/octet-stream"
        const val MIME_TYPE_TEXT_PLAIN     = "text/plain"
        const val MIME_TYPE_HTML           = "text/html"
        const val MIME_TYPE_JSON           = "application/json"
        const val MIME_TYPE_PDF            = "application/pdf"
        const val MIME_TYPE_ZIP            = "application/zip"
        const val MIME_TYPE_JPEG           = "image/jpeg"
        const val MIME_TYPE_PNG            = "image/png"
        const val MIME_TYPE_GIF            = "image/gif"
        const val MIME_TYPE_WEBP           = "image/webp"
        const val MIME_TYPE_MP4            = "video/mp4"
        const val MIME_TYPE_MP3            = "audio/mpeg"
        const val MIME_TYPE_APK            = "application/vnd.android.package-archive"

        /**
         * Static factory: resolve from a file path without a Context.
         * Falls back to URLConnection guessing — no ContentResolver available.
         */
        @JvmStatic
        fun fromPathStatic(filePath: String): String =
            fromExtensionStatic(File(filePath).extension)
                ?: URLConnection.guessContentTypeFromName(filePath)
                ?: MIME_TYPE_UNKNOWN

        /**
         * Static factory: resolve from an extension without a Context.
         */
        @JvmStatic
        fun fromExtensionStatic(extension: String): String? =
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase().trimStart('.'))

        // ── Magic-byte signatures ────────────────────────────────
        private val MAGIC_BYTES: List<MagicSignature> = listOf(
            MagicSignature(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()),              MIME_TYPE_JPEG),
            MagicSignature(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A), MIME_TYPE_PNG),
            MagicSignature(byteArrayOf(0x47, 0x49, 0x46, 0x38),                                  MIME_TYPE_GIF),
            MagicSignature(byteArrayOf(0x52, 0x49, 0x46, 0x46),                                  MIME_TYPE_WEBP, offset = 0, secondaryCheck = { it.size > 11 && String(it.sliceArray(8..11)) == "WEBP" }),
            MagicSignature(byteArrayOf(0x25, 0x50, 0x44, 0x46),                                  MIME_TYPE_PDF),
            MagicSignature(byteArrayOf(0x50, 0x4B, 0x03, 0x04),                                  MIME_TYPE_ZIP),
            MagicSignature(byteArrayOf(0x49, 0x44, 0x33),                                        MIME_TYPE_MP3),
            MagicSignature(byteArrayOf(0xFF.toByte(), 0xFB.toByte()),                             MIME_TYPE_MP3),
            MagicSignature(byteArrayOf(0x00, 0x00, 0x00),                                        MIME_TYPE_MP4, secondaryCheck = { it.size > 7 && String(it.sliceArray(4..7)).let { ftype -> ftype == "ftyp" } }),
            MagicSignature(byteArrayOf(0x7F, 0x45, 0x4C, 0x46),                                  "application/x-elf"),
        )
    }

    // ─────────────────────────────────────────────────────────────
    // Data classes
    // ─────────────────────────────────────────────────────────────

    /**
     * Holds a file-signature pattern along with an optional secondary validation lambda.
     */
    data class MagicSignature(
        val bytes: ByteArray,
        val mimeType: String,
        val offset: Int = 0,
        val secondaryCheck: ((ByteArray) -> Boolean)? = null
    ) {
        fun matches(data: ByteArray): Boolean {
            if (data.size < offset + bytes.size) return false
            for (i in bytes.indices) {
                if (data[offset + i] != bytes[i]) return false
            }
            return secondaryCheck?.invoke(data) ?: true
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MagicSignature

            if (offset != other.offset) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (mimeType != other.mimeType) return false
            if (secondaryCheck != other.secondaryCheck) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + bytes.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + (secondaryCheck?.hashCode() ?: 0)
            return result
        }
    }

    /**
     * Rich result returned by [resolveDetailed].
     */
    data class MimeResolutionResult(
        val mimeType: String,
        val source: ResolutionSource,
        val extension: String?,
        val isKnown: Boolean
    ) {
        val isImage: Boolean get() = mimeType.startsWith("image/")
        val isVideo: Boolean get() = mimeType.startsWith("video/")
        val isAudio: Boolean get() = mimeType.startsWith("audio/")
        val isText:  Boolean get() = mimeType.startsWith("text/")
        val isApplication: Boolean get() = mimeType.startsWith("application/")
    }

    enum class ResolutionSource {
        CONTENT_RESOLVER,
        MIME_TYPE_MAP,
        MAGIC_BYTES,
        URL_CONNECTION,
        FALLBACK
    }

    // ─────────────────────────────────────────────────────────────
    // Primary API
    // ─────────────────────────────────────────────────────────────

    /**
     * Resolves the MIME type from a [Uri].
     *
     * Resolution order:
     *  1. ContentResolver (handles content:// and file:// URIs)
     *  2. File extension via MimeTypeMap
     *  3. URLConnection guess
     *  4. Fallback → [MIME_TYPE_UNKNOWN]
     */
    fun fromUri(uri: Uri): String {
        // 1. ContentResolver — most reliable for content:// URIs
        val contentResolver: ContentResolver = context.contentResolver
        val fromCR = contentResolver.getType(uri)
        if (!fromCR.isNullOrBlank() && fromCR != MIME_TYPE_UNKNOWN) return fromCR

        // 2. Try extension from the URI path
        val ext = getExtensionFromUri(uri)
        if (!ext.isNullOrBlank()) {
            val fromExt = fromExtensionStatic(ext)
            if (!fromExt.isNullOrBlank()) return fromExt
        }

        // 3. URLConnection guess on the path string
        val guess = URLConnection.guessContentTypeFromName(uri.path ?: "")
        if (!guess.isNullOrBlank()) return guess

        return MIME_TYPE_UNKNOWN
    }

    /**
     * Resolves the MIME type from a [File].
     */
    fun fromFile(file: File): String {
        if (!file.exists()) return MIME_TYPE_UNKNOWN

        // 1. Extension
        val ext = file.extension
        if (ext.isNotBlank()) {
            val fromExt = fromExtensionStatic(ext)
            if (!fromExt.isNullOrBlank()) return fromExt
        }

        // 2. Magic bytes
        val fromMagic = fromBytes(file.readBytes().take(16).toByteArray())
        if (fromMagic != MIME_TYPE_UNKNOWN) return fromMagic

        // 3. URLConnection
        return URLConnection.guessContentTypeFromName(file.name) ?: MIME_TYPE_UNKNOWN
    }

    /**
     * Resolves the MIME type from a file path string.
     */
    fun fromPath(filePath: String): String = fromFile(File(filePath))

    /**
     * Resolves the MIME type from a file extension (with or without leading dot).
     * Returns [MIME_TYPE_UNKNOWN] if not found.
     */
    fun fromExtension(extension: String): String =
        fromExtensionStatic(extension) ?: MIME_TYPE_UNKNOWN

    /**
     * Resolves the MIME type by inspecting the first bytes of [data] (magic bytes).
     * Reads up to 16 bytes.
     */
    fun fromBytes(data: ByteArray): String {
        val sample = if (data.size > 16) data.copyOf(16) else data
        return MAGIC_BYTES.firstOrNull { it.matches(sample) }?.mimeType ?: MIME_TYPE_UNKNOWN
    }

    /**
     * Resolves the MIME type from a URL string.
     */
    fun fromUrl(url: String): String {
        // Try to parse as URI first
        return try {
            val uri = url.toUri()
            val fromUri = fromUri(uri)
            if (fromUri != MIME_TYPE_UNKNOWN) fromUri
            else URLConnection.guessContentTypeFromName(url) ?: MIME_TYPE_UNKNOWN
        } catch (e: Exception) {
            URLConnection.guessContentTypeFromName(url) ?: MIME_TYPE_UNKNOWN
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Reverse Lookup
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the primary file extension for a given MIME type.
     * e.g. "image/jpeg" → "jpg"
     */
    fun extensionFromMimeType(mimeType: String): String? =
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType.lowercase())

    /**
     * Returns a list of common extensions for a given MIME type by scanning
     * all registered extensions in [MimeTypeMap].
     *
     * Note: [MimeTypeMap] only exposes forward lookup (ext→mime) publicly,
     * so this performs a broad reverse scan over a curated set of common extensions.
     */
    fun extensionsFromMimeType(mimeType: String): List<String> {
        val primary = extensionFromMimeType(mimeType)
        val map = MimeTypeMap.getSingleton()
        val results = mutableSetOf<String>()
        if (primary != null) results.add(primary)

        // Scan common extensions
        val candidates = listOf(
            "jpg","jpeg","png","gif","webp","bmp","svg","ico","tiff","heic","heif",
            "mp4","mkv","avi","mov","wmv","flv","webm","3gp",
            "mp3","aac","ogg","wav","flac","m4a","wma","opus",
            "pdf","doc","docx","xls","xlsx","ppt","pptx","odt","ods","odp",
            "txt","html","htm","css","js","json","xml","csv","md",
            "zip","rar","7z","tar","gz","bz2",
            "apk","dex","so","jar",
            "ttf","otf","woff","woff2",
            "bin","exe","iso","dmg","img"
        )
        for (ext in candidates) {
            if (map.getMimeTypeFromExtension(ext) == mimeType) results.add(ext)
        }
        return results.toList()
    }

    // ─────────────────────────────────────────────────────────────
    // Category Helpers
    // ─────────────────────────────────────────────────────────────

    fun isImage(mimeType: String): Boolean = mimeType.startsWith("image/")
    fun isVideo(mimeType: String): Boolean = mimeType.startsWith("video/")
    fun isAudio(mimeType: String): Boolean = mimeType.startsWith("audio/")
    fun isText(mimeType: String): Boolean  = mimeType.startsWith("text/")
    fun isPdf(mimeType: String): Boolean   = mimeType == MIME_TYPE_PDF
    fun isZip(mimeType: String): Boolean   = mimeType == MIME_TYPE_ZIP || mimeType == "application/x-zip-compressed"
    fun isApk(mimeType: String): Boolean   = mimeType == MIME_TYPE_APK
    fun isUnknown(mimeType: String): Boolean = mimeType == MIME_TYPE_UNKNOWN

    /** Returns a human-readable category label for a MIME type. */
    fun categoryOf(mimeType: String): String = when {
        isImage(mimeType) -> "Image"
        isVideo(mimeType) -> "Video"
        isAudio(mimeType) -> "Audio"
        isText(mimeType)  -> "Text"
        isPdf(mimeType)   -> "PDF Document"
        isZip(mimeType)   -> "Archive"
        isApk(mimeType)   -> "Android Package"
        mimeType.startsWith("application/") -> "Application"
        else -> "Unknown"
    }

    // ─────────────────────────────────────────────────────────────
    // Detailed Resolution
    // ─────────────────────────────────────────────────────────────

    /**
     * Resolves the MIME type from a [Uri] and returns a rich [MimeResolutionResult]
     * that includes the resolution source and extension.
     */
    fun resolveDetailed(uri: Uri): MimeResolutionResult {
        val contentResolver = context.contentResolver

        // 1. ContentResolver
        val fromCR = contentResolver.getType(uri)
        if (!fromCR.isNullOrBlank() && fromCR != MIME_TYPE_UNKNOWN) {
            return MimeResolutionResult(
                mimeType  = fromCR,
                source    = ResolutionSource.CONTENT_RESOLVER,
                extension = extensionFromMimeType(fromCR),
                isKnown   = true
            )
        }

        // 2. Extension via MimeTypeMap
        val ext = getExtensionFromUri(uri)
        if (!ext.isNullOrBlank()) {
            val fromExt = fromExtensionStatic(ext)
            if (!fromExt.isNullOrBlank()) {
                return MimeResolutionResult(
                    mimeType  = fromExt,
                    source    = ResolutionSource.MIME_TYPE_MAP,
                    extension = ext,
                    isKnown   = true
                )
            }
        }

        // 3. URLConnection
        val fromUrl = URLConnection.guessContentTypeFromName(uri.toString())
        if (!fromUrl.isNullOrBlank()) {
            return MimeResolutionResult(
                mimeType  = fromUrl,
                source    = ResolutionSource.URL_CONNECTION,
                extension = ext,
                isKnown   = true
            )
        }

        // Fallback
        return MimeResolutionResult(
            mimeType  = MIME_TYPE_UNKNOWN,
            source    = ResolutionSource.FALLBACK,
            extension = ext,
            isKnown   = false
        )
    }

    // ─────────────────────────────────────────────────────────────
    // ContentResolver Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the display name of a content URI using [OpenableColumns].
     */
    fun getDisplayName(uri: Uri): String? {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) return uri.lastPathSegment
        return context.contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else null
        }
    }

    /**
     * Returns the file size of a content URI in bytes.
     */
    fun getFileSize(uri: Uri): Long? {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) {
            return uri.path?.let { File(it).length() }
        }
        return context.contentResolver.query(
            uri, arrayOf(OpenableColumns.SIZE), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            } else null
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    private fun getExtensionFromUri(uri: Uri): String? {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                // Try display name first
                getDisplayName(uri)?.substringAfterLast('.', "")?.takeIf { it.isNotBlank() }
                    ?: MimeTypeMap.getFileExtensionFromUrl(uri.toString())?.takeIf { it.isNotBlank() }
            }
            else -> MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                ?.takeIf { it.isNotBlank() }
                ?: uri.path?.let { File(it).extension.takeIf { ext -> ext.isNotBlank() } }
        }
    }
}