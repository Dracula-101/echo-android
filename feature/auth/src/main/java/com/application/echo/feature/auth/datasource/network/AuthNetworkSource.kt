package com.application.echo.feature.auth.datasource.network

import com.application.echo.core.api.auth.AuthLoginResult
import com.application.echo.core.api.auth.AuthRegisterResult

interface AuthNetworkSource {

    suspend fun login(email: String, password: String): AuthLoginResult

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult
}
