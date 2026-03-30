package com.application.echo.api.message

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.ApiValidationErrorField
import com.application.echo.core.network.model.NetworkException

/**
 * Domain-specific error hierarchy for all messaging operations.
 *
 * Each subtype maps to a backend error code from the message service.
 * The [fromApiError] factory converts raw [ApiError] codes into the
 * appropriate sealed subtype, enabling exhaustive `when` handling
 * without string comparisons.
 */
sealed class MessageError(
    open val code: String,
    open val message: String,
) {

    // ── User ────────────────────────────────────────────────────────

    data class UserBlocked(
        override val message: String,
    ) : MessageError("USER_BLOCKED", message)

    data class BlockUserNotFound(
        override val message: String,
    ) : MessageError("BLOCK_USER_NOT_FOUND", message)

    data class RateLimitExceeded(
        override val message: String,
    ) : MessageError("RATE_LIMIT_EXCEEDED", message)

    // ── Conversation ────────────────────────────────────────────────

    data class ConversationNotFound(
        override val message: String,
    ) : MessageError("CONVERSATION_NOT_FOUND", message)

    data class ConversationValidationFailed(
        override val message: String,
    ) : MessageError("CONVERSATION_VALIDATION_FAILED", message)

    data class ConversationInactive(
        override val message: String,
    ) : MessageError("CONVERSATION_INACTIVE", message)

    data class ConversationCreateFailed(
        override val message: String,
    ) : MessageError("CONVERSATION_CREATE_FAILED", message)

    data class ConversationCreationFailed(
        override val message: String,
    ) : MessageError("CONVERSATION_CREATION_FAILED", message)

    data class ConversationFetchFailed(
        override val message: String,
    ) : MessageError("CONVERSATION_FETCH_FAILED", message)

    // ── Participant ─────────────────────────────────────────────────

    data class ParticipantNotInConversation(
        override val message: String,
    ) : MessageError("PARTICIPANT_NOT_IN_CONVERSATION", message)

    data class ParticipantLeftConversation(
        override val message: String,
    ) : MessageError("PARTICIPANT_LEFT_CONVERSATION", message)

    data class ParticipantRemovedFromConversation(
        override val message: String,
    ) : MessageError("PARTICIPANT_REMOVED_FROM_CONVERSATION", message)

    data class ParticipantNotAllowedToSendMessages(
        override val message: String,
    ) : MessageError("PARTICIPANT_NOT_ALLOWED_TO_SEND_MESSAGES", message)

    // ── Message ─────────────────────────────────────────────────────

    data class EmptyMessageContent(
        override val message: String,
    ) : MessageError("EMPTY_MESSAGE_CONTENT", message)

    data class MissingMessageMetadata(
        override val message: String,
    ) : MessageError("MISSING_MESSAGE_METADATA", message)

    data class MessageEventPublishFailed(
        override val message: String,
    ) : MessageError("MESSAGE_EVENT_PUBLISH_FAILED", message)

    data class MessageRetrievalFailed(
        override val message: String,
    ) : MessageError("MESSAGE_RETRIEVAL_FAILED", message)

    // ── Validation (field-level errors from the server) ─────────────

    data class ValidationFailed(
        override val message: String,
        val fields: List<ApiValidationErrorField>,
    ) : MessageError("VALIDATION_FAILED", message)

    // ── Network / Transport ─────────────────────────────────────────

    /** Connectivity, timeout, or serialization failure — no backend code. */
    data class NetworkError(
        override val message: String,
        val cause: NetworkException,
    ) : MessageError("NETWORK_ERROR", message)

    // ── Unknown ─────────────────────────────────────────────────────

    /** Fallback for error codes the client doesn't recognize yet. */
    data class Unknown(
        override val code: String,
        override val message: String,
    ) : MessageError(code, message)

    companion object {

        /**
         * Maps a raw [ApiError] (parsed from an HTTP error body) to a
         * typed [MessageError]. Falls back to [Unknown] for unrecognized codes.
         */
        fun fromApiError(apiError: ApiError): MessageError = when (apiError.code) {
            // User
            "USER_BLOCKED" -> UserBlocked(apiError.message)
            "BLOCK_USER_NOT_FOUND" -> BlockUserNotFound(apiError.message)
            "RATE_LIMIT_EXCEEDED" -> RateLimitExceeded(apiError.message)

            // Conversation
            "CONVERSATION_NOT_FOUND" -> ConversationNotFound(apiError.message)
            "CONVERSATION_VALIDATION_FAILED" -> ConversationValidationFailed(apiError.message)
            "CONVERSATION_INACTIVE" -> ConversationInactive(apiError.message)
            "CONVERSATION_CREATE_FAILED" -> ConversationCreateFailed(apiError.message)
            "CONVERSATION_CREATION_FAILED" -> ConversationCreationFailed(apiError.message)
            "CONVERSATION_FETCH_FAILED" -> ConversationFetchFailed(apiError.message)

            // Participant
            "PARTICIPANT_NOT_IN_CONVERSATION" -> ParticipantNotInConversation(apiError.message)
            "PARTICIPANT_LEFT_CONVERSATION" -> ParticipantLeftConversation(apiError.message)
            "PARTICIPANT_REMOVED_FROM_CONVERSATION" -> ParticipantRemovedFromConversation(apiError.message)
            "PARTICIPANT_NOT_ALLOWED_TO_SEND_MESSAGES" -> ParticipantNotAllowedToSendMessages(apiError.message)

            // Message
            "EMPTY_MESSAGE_CONTENT" -> EmptyMessageContent(apiError.message)
            "MISSING_MESSAGE_METADATA" -> MissingMessageMetadata(apiError.message)
            "MESSAGE_EVENT_PUBLISH_FAILED" -> MessageEventPublishFailed(apiError.message)
            "MESSAGE_RETRIEVAL_FAILED" -> MessageRetrievalFailed(apiError.message)

            // Validation (shared code)
            "VALIDATION_FAILED", "VALIDATION_ERROR" -> ValidationFailed(
                message = apiError.message,
                fields = apiError.fields.orEmpty(),
            )

            // Unknown
            else -> Unknown(code = apiError.code, message = apiError.message)
        }

        /**
         * Wraps a [NetworkException] (no backend error code available).
         */
        fun fromNetworkException(exception: NetworkException): MessageError = when (exception) {
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
