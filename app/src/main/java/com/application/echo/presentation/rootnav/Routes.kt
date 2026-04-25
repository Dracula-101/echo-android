package com.application.echo.presentation.rootnav

import kotlinx.serialization.Serializable

@Serializable
data object SplashScreen

@Serializable
data object LoginScreen

@Serializable
data object PhoneAuthScreen

@Serializable
data object OtpScreen

@Serializable
data object RegisterScreen

@Serializable
data object CreateProfile

@Serializable
data object ConversationScreen

@Serializable
data object SearchUserScreen

@Serializable
data class ChatScreen(val conversationId: String, val participantName: String = "")