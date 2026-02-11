package com.application.echo.core.api.auth

import com.application.echo.core.api.common.ApiConstants
import com.application.echo.core.api.common.HealthResponse
import com.application.echo.core.network.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service definition for the Auth API.
 *
 * Internal — consumers use [AuthRepository] instead.
 */
internal interface AuthApiService {

    @POST(ApiConstants.AUTH_LOGIN)
    suspend fun login(
        @Body request: LoginRequest,
    ): NetworkResponse<LoginResponse>

    @POST(ApiConstants.AUTH_REGISTER)
    suspend fun register(
        @Body request: RegisterRequest,
    ): NetworkResponse<RegisterResponse>

    @POST(ApiConstants.AUTH_REFRESH_TOKEN)
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest,
    ): NetworkResponse<RefreshTokenResponse>

    @GET(ApiConstants.AUTH_HEALTH)
    suspend fun health(): NetworkResponse<HealthResponse>
}
