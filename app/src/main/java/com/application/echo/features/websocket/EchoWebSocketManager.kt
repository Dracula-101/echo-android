package com.application.echo.features.websocket

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.application.echo.BuildConfig
import com.application.echo.api.session.SessionProvider
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.websocket.handler.MessageHandlerRegistry
import com.application.echo.core.websocket.handler.TypedMessageHandler
import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketMessage
import com.application.echo.core.websocket.model.WebSocketState
import com.application.echo.core.websocket.session.WebSocketSession
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.websocket.model.ClientEventType
import com.application.echo.features.websocket.model.ClientInfo
import com.application.echo.features.websocket.model.ClientMessage
import com.application.echo.features.websocket.model.DeliveryAckPayload
import com.application.echo.features.websocket.model.PingNetworkInfo
import com.application.echo.features.websocket.model.PingPayload
import com.application.echo.features.websocket.model.PresenceUpdatePayload
import com.application.echo.features.websocket.model.ReadAckPayload
import com.application.echo.features.websocket.model.SubscribeFilters
import com.application.echo.features.websocket.model.SubscribePayload
import com.application.echo.features.websocket.model.TypingPayload
import com.application.echo.features.websocket.model.UnsubscribeFilters
import com.application.echo.features.websocket.model.UnsubscribePayload
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoWebSocketManager @Inject constructor(
    private val session: WebSocketSession,
    private val registry: MessageHandlerRegistry,
    private val authRepository: AuthRepository,
    private val sessionProvider: SessionProvider,
    private val gson: Gson,
    handlers: @JvmSuppressWildcards Set<TypedMessageHandler>,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            if (authRepository.authStateFlow.value is AuthState.Authenticated) {
                Timber.tag(TAG).i("App foregrounded — sending presence online")
                sendPresenceUpdate(PRESENCE_ONLINE)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            if (authRepository.authStateFlow.value is AuthState.Authenticated) {
                Timber.tag(TAG).i("App backgrounded — sending presence away")
                sendPresenceUpdate(PRESENCE_AWAY)
            }
        }
    }

    init {
        Timber.tag(TAG).i("Initializing EchoWebSocketManager")
        handlers.forEach { handler ->
            registry.register(handler)
            Timber.tag(TAG).d("Registered handler: %s", handler.messageType)
        }
        Timber.tag(TAG).i("Registered %d message handler(s)", handlers.size)
        observeAuthState()
        observeConnectionState()
        observeEvents()
        observeMessages()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    // ──────────────── Observers ────────────────

    private fun observeAuthState() {
        authRepository.authStateFlow
            .distinctUntilChangedBy { it::class }
            .onEach { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        Timber.tag(TAG).i(
                            "Auth state -> Authenticated (user=%s) — connecting WebSocket",
                            state.user.userId,
                        )
                        session.connect()
                    }

                    is AuthState.Unauthenticated -> {
                        Timber.tag(TAG).i(
                            "Auth state -> Unauthenticated (reason=%s) — disconnecting WebSocket",
                            state.reason,
                        )
                        session.disconnect()
                    }

                    is AuthState.CreateProfile -> {
                        Timber.tag(TAG).d("Auth state -> CreateProfile — WebSocket idle")
                    }

                    is AuthState.Initializing -> {
                        Timber.tag(TAG).d("Auth state -> Initializing — WebSocket idle")
                    }
                }
            }
            .launchIn(scope)
    }

    private fun observeConnectionState() {
        session.state
            .onEach { state ->
                when (state) {
                    is WebSocketState.Connected ->
                        Timber.tag(TAG).i("Connection state -> Connected (%s)", state.url)
                    is WebSocketState.Connecting ->
                        Timber.tag(TAG).d("Connection state -> Connecting")
                    is WebSocketState.Disconnected ->
                        Timber.tag(TAG).i("Connection state -> Disconnected")
                    is WebSocketState.Disconnecting ->
                        Timber.tag(TAG).d("Connection state -> Disconnecting")
                    is WebSocketState.Reconnecting ->
                        Timber.tag(TAG).w(
                            "Connection state -> Reconnecting (attempt %d/%d)",
                            state.attempt,
                            state.maxRetries,
                        )
                    is WebSocketState.Failed ->
                        Timber.tag(TAG).e(state.exception.throwable)
                }
            }
            .launchIn(scope)
    }

    private fun observeEvents() {
        session.events
            .onEach { event ->
                when (event) {
                    is WebSocketEvent.OnConnected ->
                        Timber.tag(TAG).i("Event: Connected to %s", event.url)
                    is WebSocketEvent.OnDisconnected ->
                        Timber.tag(TAG).i("Event: Disconnected (code=%d, reason=%s)", event.code, event.reason)
                    is WebSocketEvent.OnError ->
                        Timber.tag(TAG).e(event.exception.throwable)
                    is WebSocketEvent.OnConnectionLost ->
                        Timber.tag(TAG).w("Event: Connection lost")
                    is WebSocketEvent.OnReconnecting ->
                        Timber.tag(TAG).d("Event: Reconnecting (attempt %d)", event.attempt)
                    is WebSocketEvent.OnPong ->
                        Timber.tag(TAG).v("Event: Pong received")
                    is WebSocketEvent.OnMessage ->
                        Timber.tag(TAG).v("Event: Message received")
                }
            }
            .launchIn(scope)
    }

    private fun observeMessages() {
        session.messages
            .onEach { message ->
                when (message) {
                    is WebSocketMessage.Text -> {
                        Timber.tag(TAG).d("Dispatching message (%d chars)", message.payload.length)
                    }
                    is WebSocketMessage.Binary -> {
                        Timber.tag(TAG).d("Dispatching binary message (%d bytes)", message.payload.size)
                    }
                }
                registry.dispatch(message)
            }
            .launchIn(scope)
    }

    // ──────────────── Send Helpers ────────────────

    fun <T> send(
        type: String,
        payload: T? = null,
        idempotencyKey: String? = null,
    ): Boolean {
        val message = ClientMessage(
            type = type,
            idempotencyKey = idempotencyKey,
            clientInfo = buildClientInfo(),
            payload = payload,
        )
        val json = gson.toJson(message)
        val success = session.send(WebSocketMessage.Text(json))
        if (success) {
            Timber.tag(TAG).d("Sent [%s] (%d chars)", type, json.length)
        } else {
            Timber.tag(TAG).w("Failed to send [%s] — session not connected", type)
        }
        return success
    }

    fun subscribe(
        topics: List<String>,
        conversationIds: List<String>? = null,
        userId: String? = null,
    ): Boolean {
        Timber.tag(TAG).i("Subscribing to topics: %s", topics.joinToString())
        return send(
            type = ClientEventType.SUBSCRIBE,
            payload = SubscribePayload(
                topics = topics,
                filters = SubscribeFilters(
                    conversationIds = conversationIds,
                    userId = userId,
                ),
            ),
        )
    }

    fun unsubscribe(
        topics: List<String>,
        conversationIds: List<String>? = null,
    ): Boolean {
        Timber.tag(TAG).i("Unsubscribing from topics: %s", topics.joinToString())
        return send(
            type = ClientEventType.UNSUBSCRIBE,
            payload = UnsubscribePayload(
                topics = topics,
                filters = UnsubscribeFilters(conversationIds = conversationIds),
            ),
        )
    }

    fun sendPresenceUpdate(
        status: String,
        customStatus: String? = null,
    ): Boolean {
        Timber.tag(TAG).d("Sending presence update: %s", status)
        return send(
            type = ClientEventType.PRESENCE_UPDATE,
            payload = PresenceUpdatePayload(
                status = status,
                customStatus = customStatus,
            ),
        )
    }

    fun sendTypingStart(conversationId: String): Boolean {
        Timber.tag(TAG).v("Sending typing.start for conversation %s", conversationId)
        return send(
            type = ClientEventType.TYPING_START,
            payload = TypingPayload(conversationId = conversationId),
        )
    }

    fun sendTypingStop(conversationId: String): Boolean {
        Timber.tag(TAG).v("Sending typing.stop for conversation %s", conversationId)
        return send(
            type = ClientEventType.TYPING_STOP,
            payload = TypingPayload(conversationId = conversationId),
        )
    }

    fun markDelivered(
        conversationId: String,
        messageIds: List<String>,
    ): Boolean {
        Timber.tag(TAG).d(
            "Marking %d message(s) delivered in conversation %s",
            messageIds.size,
            conversationId,
        )
        return send(
            type = ClientEventType.MARK_DELIVERED,
            payload = DeliveryAckPayload(
                conversationId = conversationId,
                messageIds = messageIds,
                deliveredAt = Clock.System.now().toString(),
            ),
        )
    }

    fun markRead(
        conversationId: String,
        messageIds: List<String>,
    ): Boolean {
        Timber.tag(TAG).d(
            "Marking %d message(s) read in conversation %s",
            messageIds.size,
            conversationId,
        )
        return send(
            type = ClientEventType.MARK_READ,
            payload = ReadAckPayload(
                conversationId = conversationId,
                messageIds = messageIds,
                readAt = Clock.System.now().toString(),
            ),
        )
    }

    fun sendPing(connectionType: String? = null): Boolean {
        Timber.tag(TAG).v("Sending ping")
        return send(
            type = ClientEventType.PING,
            payload = PingPayload(
                clientTimestamp = Clock.System.now().toString(),
                networkInfo = connectionType?.let { PingNetworkInfo(it) },
            ),
        )
    }

    // ──────────────── Internals ────────────────

    private fun buildClientInfo(): ClientInfo {
        val device = sessionProvider.deviceInfo
        return ClientInfo(
            appVersion = BuildConfig.VERSION_NAME,
            deviceId = device.deviceId,
            deviceModel = device.deviceName,
        )
    }

    private companion object {
        const val TAG = "EchoWS"
        const val PRESENCE_ONLINE = "online"
        const val PRESENCE_AWAY = "away"
    }
}
