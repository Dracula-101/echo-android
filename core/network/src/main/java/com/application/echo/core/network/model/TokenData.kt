package com.application.echo.core.network.model

data class TokenData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)