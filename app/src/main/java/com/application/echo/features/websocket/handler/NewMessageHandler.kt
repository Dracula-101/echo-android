package com.application.echo.features.websocket.handler

import com.application.echo.api.message.MessageResponse
import com.application.echo.core.websocket.handler.TypedMessageHandler
import com.application.echo.features.messaging.repository.MessagingRepository
import com.application.echo.features.websocket.model.ServerEventType
import com.application.echo.features.websocket.model.ServerMessage
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

class NewMessageHandler @Inject constructor(
    private val gson: Gson,
    private val messagingRepository: dagger.Lazy<MessagingRepository>,
) : TypedMessageHandler {

    override val messageType: String = ServerEventType.MESSAGE_NEW

    override suspend fun handle(payload: String) {
        val message = try {
            gson.fromJson(payload, ServerMessage::class.java)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to parse ServerMessage envelope")
            return
        }

        val data = message.payload?.let { gson.fromJson(it, MessageResponse::class.java) }
        if (data != null) {
            Timber.tag(TAG).d(
                "[%s] New message — id=%s, conversation=%s, sender=%s",
                message.id,
                data.id,
                data.conversationId,
                data.senderId,
            )
            messagingRepository.get().onNewMessageReceived(data)
        } else {
            Timber.tag(TAG).w("[%s] message.new event received with empty payload", message.id)
        }
    }

    private companion object {
        const val TAG = "EchoWS"
    }
}
