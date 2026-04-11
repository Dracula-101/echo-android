package com.application.echo.features.messaging.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransientMessagingState @Inject constructor() {

    private val _typing = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val typing: StateFlow<Map<String, Set<String>>> = _typing.asStateFlow()

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    fun setTyping(conversationId: String, userId: String, isTyping: Boolean) {
        _typing.update { current ->
            val users = current[conversationId].orEmpty()
            val updated = if (isTyping) users + userId else users - userId
            if (updated.isEmpty()) current - conversationId
            else current + (conversationId to updated)
        }
    }

    fun setActiveConversation(conversationId: String?) {
        _activeConversationId.value = conversationId
    }

    fun clear() {
        _typing.value = emptyMap()
        _activeConversationId.value = null
    }
}
