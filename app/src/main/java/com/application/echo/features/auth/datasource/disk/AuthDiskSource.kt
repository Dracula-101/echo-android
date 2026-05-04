package com.application.echo.features.auth.datasource.disk

import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.flow.Flow

interface AuthDiskSource {
    var userState: UserState
    val userStateFlow: Flow<UserState>

    var sessionId: String?
    var sessionToken: String?

    var registerEmail: String?
    val registerEmailStateFlow: Flow<String?>

    var registerPassword: String?
    val registerPasswordStateFlow: Flow<String?>
}