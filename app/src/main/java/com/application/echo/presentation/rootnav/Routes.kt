package com.application.echo.presentation.rootnav

import kotlinx.serialization.Serializable

@Serializable data object LoginScreenRoute
@Serializable data object PhoneAuthScreenRoute
@Serializable data object OtpScreenRoute
@Serializable data object CreateProfileRoute
@Serializable data object ConversationScreenRoute
@Serializable data object SearchUserScreenRoute
@Serializable data class ChatScreenRoute(
    val conversationId: String,
    val participantName: String = "",
)