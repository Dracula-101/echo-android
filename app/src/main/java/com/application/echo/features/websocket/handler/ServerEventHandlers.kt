package com.application.echo.features.websocket.handler

import com.application.echo.core.websocket.handler.TypedMessageHandler
import com.application.echo.features.websocket.model.MessageReadPayload
import com.application.echo.features.websocket.model.PresenceChangedPayload
import com.application.echo.features.websocket.model.ServerEventType
import com.application.echo.features.websocket.model.ServerMessage
import com.application.echo.features.websocket.model.SubscribedPayload
import com.application.echo.features.websocket.model.SyncRequiredPayload
import com.application.echo.features.websocket.model.TypingEventPayload
import com.application.echo.features.websocket.model.WsErrorPayload
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "EchoWS"

class SubscribedHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.SUBSCRIBED

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, SubscribedPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).i(
                "[%s] Subscribed — %d topic(s): [%s], %d active subscription(s)",
                message.id,
                data.topics.size,
                data.topics.joinToString(),
                data.subscriptionCount,
            )
            data.subscriptions.forEach { sub ->
                Timber.tag(TAG).d(
                    "[%s]   topic=%s, resourceId=%s, active=%s",
                    message.id,
                    sub.topic,
                    sub.resourceId ?: "n/a",
                    sub.active,
                )
            }
        } else {
            Timber.tag(TAG).w("[%s] Subscribed event received with empty payload", message.id)
        }
    }
}

class TypingStartHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.TYPING_START

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, TypingEventPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).d(
                "[%s] Typing started — user=%s, conversation=%s, timestamp=%s",
                message.id,
                data.userId,
                data.conversationId,
                data.timestamp,
            )
        } else {
            Timber.tag(TAG).w("[%s] typing.start event received with empty payload", message.id)
        }
    }
}

class TypingStopHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.TYPING_STOP

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, TypingEventPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).d(
                "[%s] Typing stopped — user=%s, conversation=%s, timestamp=%s",
                message.id,
                data.userId,
                data.conversationId,
                data.timestamp,
            )
        } else {
            Timber.tag(TAG).w("[%s] typing.stop event received with empty payload", message.id)
        }
    }
}

class PresenceChangedHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.PRESENCE_CHANGED

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, PresenceChangedPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).i(
                "[%s] Presence changed — user=%s, %s -> %s, customStatus=%s, changedAt=%s",
                message.id,
                data.userId,
                data.oldStatus,
                data.newStatus,
                data.customStatus ?: "none",
                data.changedAt,
            )
        } else {
            Timber.tag(TAG).w("[%s] presence.changed event received with empty payload", message.id)
        }
    }
}

class MessageReadHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.MESSAGE_READ

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, MessageReadPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).d(
                "[%s] Message read — user=%s, conversation=%s, messageIds=%s, readAt=%s",
                message.id,
                data.userId,
                data.conversationId,
                data.messageIds.joinToString(),
                data.readAt,
            )
        } else {
            Timber.tag(TAG).w("[%s] message.read event received with empty payload", message.id)
        }
    }
}

class SyncRequiredHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.SYNC_REQUIRED

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, SyncRequiredPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).w(
                "[%s] Sync required — %d conversation(s) need re-fetch",
                message.id,
                data.conversations.size,
            )
            data.conversations.forEach { conv ->
                Timber.tag(TAG).w(
                    "[%s]   conversation=%s, unread=%d, lastMessageId=%s, lastMessageAt=%s",
                    message.id,
                    conv.conversationId,
                    conv.unreadCount,
                    conv.lastMessageId,
                    conv.lastMessageAt,
                )
            }
        } else {
            Timber.tag(TAG).w("[%s] sync.required event received with empty payload", message.id)
        }
    }
}

class WsErrorHandler @Inject constructor(
    private val gson: Gson,
) : TypedMessageHandler {
    override val messageType: String = ServerEventType.ERROR

    override suspend fun handle(payload: String) {
        val message = parseMessage(payload) ?: return
        val data = message.payload?.let { gson.fromJson(it, WsErrorPayload::class.java) }
        if (data != null) {
            Timber.tag(TAG).e(
                "[%s] Server error — code=%s, message=%s, retryable=%s, requestId=%s",
                message.id,
                data.code,
                data.message,
                data.retryable,
                data.requestId ?: "n/a",
            )
        } else {
            Timber.tag(TAG).e(
                "[%s] Server error event received with empty payload (status=%s)",
                message.id,
                message.status,
            )
        }
    }
}

// ──────────────── Shared Helpers ────────────────

private fun parseMessage(payload: String): ServerMessage? {
    return try {
        Gson().fromJson(payload, ServerMessage::class.java)
    } catch (e: Exception) {
        Timber.tag(TAG).e(e, "Failed to parse ServerMessage envelope")
        null
    }
}
