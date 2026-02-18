package com.application.echo.feature.auth.datasource.network

interface AuthNetworkSource {

    suspend fun login(email: String, password: String): Result<String>

}