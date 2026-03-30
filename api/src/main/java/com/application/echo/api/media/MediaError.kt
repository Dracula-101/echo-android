package com.application.echo.api.media

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.ApiValidationErrorField
import com.application.echo.core.network.model.NetworkException

/**
 * Domain-specific error hierarchy for all media operations.
 *
 * Each subtype maps to a backend error code from the media service.
 * The [fromApiError] factory converts raw [ApiError] codes into the
 * appropriate sealed subtype, enabling exhaustive `when` handling
 * without string comparisons.
 */
sealed class MediaError(
    open val code: String,
    open val message: String,
) {

    // ── Upload ──────────────────────────────────────────────────────

    data class UploadFailed(
        override val message: String,
    ) : MediaError("MEDIA_UPLOAD_FAILED", message)

    data class FileTooLarge(
        override val message: String,
    ) : MediaError("MEDIA_FILE_TOO_LARGE", message)

    data class InvalidFileType(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_FILE_TYPE", message)

    data class InvalidFileName(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_FILE_NAME", message)

    data class UploadTimeout(
        override val message: String,
    ) : MediaError("MEDIA_UPLOAD_TIMEOUT", message)

    data class StorageQuotaExceeded(
        override val message: String,
    ) : MediaError("MEDIA_STORAGE_QUOTA_EXCEEDED", message)

    data class ConversationNotFound(
        override val message: String,
    ) : MediaError("MEDIA_CONVERSATION_NOT_FOUND", message)

    // ── Download ────────────────────────────────────────────────────

    data class DownloadFailed(
        override val message: String,
    ) : MediaError("MEDIA_DOWNLOAD_FAILED", message)

    data class FileNotFound(
        override val message: String,
    ) : MediaError("MEDIA_FILE_NOT_FOUND", message)

    data class FileExpired(
        override val message: String,
    ) : MediaError("MEDIA_FILE_EXPIRED", message)

    data class AccessDenied(
        override val message: String,
    ) : MediaError("MEDIA_ACCESS_DENIED", message)

    data class InvalidAccessToken(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_ACCESS_TOKEN", message)

    // ── Processing ──────────────────────────────────────────────────

    data class ProcessingFailed(
        override val message: String,
    ) : MediaError("MEDIA_PROCESSING_FAILED", message)

    data class ThumbnailGenerationFailed(
        override val message: String,
    ) : MediaError("MEDIA_THUMBNAIL_GENERATION_FAILED", message)

    data class TranscodingFailed(
        override val message: String,
    ) : MediaError("MEDIA_TRANSCODING_FAILED", message)

    data class CompressionFailed(
        override val message: String,
    ) : MediaError("MEDIA_COMPRESSION_FAILED", message)

    // ── Security ────────────────────────────────────────────────────

    data class VirusScanFailed(
        override val message: String,
    ) : MediaError("MEDIA_VIRUS_SCAN_FAILED", message)

    data class VirusDetected(
        override val message: String,
    ) : MediaError("MEDIA_VIRUS_DETECTED", message)

    data class ModerationFailed(
        override val message: String,
    ) : MediaError("MEDIA_MODERATION_FAILED", message)

    data class ContentRejected(
        override val message: String,
    ) : MediaError("MEDIA_CONTENT_REJECTED", message)

    data class NsfwDetected(
        override val message: String,
    ) : MediaError("MEDIA_NSFW_DETECTED", message)

    // ── Album ───────────────────────────────────────────────────────

    data class AlbumNotFound(
        override val message: String,
    ) : MediaError("MEDIA_ALBUM_NOT_FOUND", message)

    data class AlbumCreationFailed(
        override val message: String,
    ) : MediaError("MEDIA_ALBUM_CREATION_FAILED", message)

    data class AlbumLimitExceeded(
        override val message: String,
    ) : MediaError("MEDIA_ALBUM_LIMIT_EXCEEDED", message)

    data class FileAlreadyInAlbum(
        override val message: String,
    ) : MediaError("MEDIA_FILE_ALREADY_IN_ALBUM", message)

    // ── Sticker ─────────────────────────────────────────────────────

    data class StickerPackNotFound(
        override val message: String,
    ) : MediaError("MEDIA_STICKER_PACK_NOT_FOUND", message)

    data class StickerNotFound(
        override val message: String,
    ) : MediaError("MEDIA_STICKER_NOT_FOUND", message)

    data class StickerLimitExceeded(
        override val message: String,
    ) : MediaError("MEDIA_STICKER_LIMIT_EXCEEDED", message)

    // ── Share ───────────────────────────────────────────────────────

    data class ShareCreationFailed(
        override val message: String,
    ) : MediaError("MEDIA_SHARE_CREATION_FAILED", message)

    data class ShareNotFound(
        override val message: String,
    ) : MediaError("MEDIA_SHARE_NOT_FOUND", message)

    data class ShareExpired(
        override val message: String,
    ) : MediaError("MEDIA_SHARE_EXPIRED", message)

    data class ShareLimitExceeded(
        override val message: String,
    ) : MediaError("MEDIA_SHARE_LIMIT_EXCEEDED", message)

    // ── Validation ──────────────────────────────────────────────────

    data class InvalidMetadata(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_METADATA", message)

    data class InvalidDimensions(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_DIMENSIONS", message)

    data class InvalidDuration(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_DURATION", message)

    data class InvalidFormat(
        override val message: String,
    ) : MediaError("MEDIA_INVALID_FORMAT", message)

    data class CorruptedFile(
        override val message: String,
    ) : MediaError("MEDIA_CORRUPTED_FILE", message)

    data class ValidationFailed(
        override val message: String,
        val fields: List<ApiValidationErrorField>,
    ) : MediaError("VALIDATION_FAILED", message)

    // ── General ─────────────────────────────────────────────────────

    data class InternalError(
        override val message: String,
    ) : MediaError("MEDIA_INTERNAL_ERROR", message)

    data class ServiceUnavailable(
        override val message: String,
    ) : MediaError("MEDIA_SERVICE_UNAVAILABLE", message)

    // ── Network / Transport ─────────────────────────────────────────

    /** Connectivity, timeout, or serialization failure — no backend code. */
    data class NetworkError(
        override val message: String,
        val cause: NetworkException,
    ) : MediaError("NETWORK_ERROR", message)

    // ── Unknown ─────────────────────────────────────────────────────

    /** Fallback for error codes the client doesn't recognize yet. */
    data class Unknown(
        override val code: String,
        override val message: String,
    ) : MediaError(code, message)

    companion object {

        /**
         * Maps a raw [ApiError] (parsed from an HTTP error body) to a
         * typed [MediaError]. Falls back to [Unknown] for unrecognized codes.
         */
        fun fromApiError(apiError: ApiError): MediaError = when (apiError.code) {
            // Upload
            "MEDIA_UPLOAD_FAILED" -> UploadFailed(apiError.message)
            "MEDIA_FILE_TOO_LARGE" -> FileTooLarge(apiError.message)
            "MEDIA_INVALID_FILE_TYPE" -> InvalidFileType(apiError.message)
            "MEDIA_INVALID_FILE_NAME" -> InvalidFileName(apiError.message)
            "MEDIA_UPLOAD_TIMEOUT" -> UploadTimeout(apiError.message)
            "MEDIA_STORAGE_QUOTA_EXCEEDED" -> StorageQuotaExceeded(apiError.message)
            "MEDIA_CONVERSATION_NOT_FOUND" -> ConversationNotFound(apiError.message)

            // Download
            "MEDIA_DOWNLOAD_FAILED" -> DownloadFailed(apiError.message)
            "MEDIA_FILE_NOT_FOUND" -> FileNotFound(apiError.message)
            "MEDIA_FILE_EXPIRED" -> FileExpired(apiError.message)
            "MEDIA_ACCESS_DENIED" -> AccessDenied(apiError.message)
            "MEDIA_INVALID_ACCESS_TOKEN" -> InvalidAccessToken(apiError.message)

            // Processing
            "MEDIA_PROCESSING_FAILED" -> ProcessingFailed(apiError.message)
            "MEDIA_THUMBNAIL_GENERATION_FAILED" -> ThumbnailGenerationFailed(apiError.message)
            "MEDIA_TRANSCODING_FAILED" -> TranscodingFailed(apiError.message)
            "MEDIA_COMPRESSION_FAILED" -> CompressionFailed(apiError.message)

            // Security
            "MEDIA_VIRUS_SCAN_FAILED" -> VirusScanFailed(apiError.message)
            "MEDIA_VIRUS_DETECTED" -> VirusDetected(apiError.message)
            "MEDIA_MODERATION_FAILED" -> ModerationFailed(apiError.message)
            "MEDIA_CONTENT_REJECTED" -> ContentRejected(apiError.message)
            "MEDIA_NSFW_DETECTED" -> NsfwDetected(apiError.message)

            // Album
            "MEDIA_ALBUM_NOT_FOUND" -> AlbumNotFound(apiError.message)
            "MEDIA_ALBUM_CREATION_FAILED" -> AlbumCreationFailed(apiError.message)
            "MEDIA_ALBUM_LIMIT_EXCEEDED" -> AlbumLimitExceeded(apiError.message)
            "MEDIA_FILE_ALREADY_IN_ALBUM" -> FileAlreadyInAlbum(apiError.message)

            // Sticker
            "MEDIA_STICKER_PACK_NOT_FOUND" -> StickerPackNotFound(apiError.message)
            "MEDIA_STICKER_NOT_FOUND" -> StickerNotFound(apiError.message)
            "MEDIA_STICKER_LIMIT_EXCEEDED" -> StickerLimitExceeded(apiError.message)

            // Share
            "MEDIA_SHARE_CREATION_FAILED" -> ShareCreationFailed(apiError.message)
            "MEDIA_SHARE_NOT_FOUND" -> ShareNotFound(apiError.message)
            "MEDIA_SHARE_EXPIRED" -> ShareExpired(apiError.message)
            "MEDIA_SHARE_LIMIT_EXCEEDED" -> ShareLimitExceeded(apiError.message)

            // Validation
            "MEDIA_INVALID_METADATA" -> InvalidMetadata(apiError.message)
            "MEDIA_INVALID_DIMENSIONS" -> InvalidDimensions(apiError.message)
            "MEDIA_INVALID_DURATION" -> InvalidDuration(apiError.message)
            "MEDIA_INVALID_FORMAT" -> InvalidFormat(apiError.message)
            "MEDIA_CORRUPTED_FILE" -> CorruptedFile(apiError.message)
            "VALIDATION_FAILED", "VALIDATION_ERROR" -> ValidationFailed(
                message = apiError.message,
                fields = apiError.fields.orEmpty(),
            )

            // General
            "MEDIA_INTERNAL_ERROR" -> InternalError(apiError.message)
            "MEDIA_SERVICE_UNAVAILABLE" -> ServiceUnavailable(apiError.message)

            // Unknown
            else -> Unknown(code = apiError.code, message = apiError.message)
        }

        /**
         * Wraps a [NetworkException] (no backend error code available).
         */
        fun fromNetworkException(exception: NetworkException): MediaError = when (exception) {
            is NetworkException.Http -> {
                val apiError = exception.error
                if (apiError != null) {
                    fromApiError(apiError)
                } else {
                    Unknown(
                        code = "HTTP_${exception.code}",
                        message = exception.errorMessage,
                    )
                }
            }
            is NetworkException.Network -> NetworkError(exception.message, exception)
            is NetworkException.Timeout -> NetworkError(exception.message, exception)
            is NetworkException.Serialization -> NetworkError(exception.message, exception)
            is NetworkException.Unknown -> NetworkError(exception.message, exception)
        }
    }
}
