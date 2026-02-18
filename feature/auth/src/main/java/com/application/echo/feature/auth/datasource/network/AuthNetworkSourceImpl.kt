package com.application.echo.feature.auth.datasource.network

import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.network.result.map
import javax.inject.Inject

class AuthNetworkSourceImpl @Inject constructor(
    private val api: AuthApiRepository,
) : AuthNetworkSource {

    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        TODO("Not yet implemented")
    }

}