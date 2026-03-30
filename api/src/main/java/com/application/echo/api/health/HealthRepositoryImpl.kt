package com.application.echo.api.health

import com.application.echo.api.auth.AuthApiService
import com.application.echo.api.common.HealthResponse
import com.application.echo.api.media.MediaApiService
import com.application.echo.api.message.MessageApiService
import com.application.echo.api.profile.ProfileApiService
import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Default [HealthRepository] that delegates to individual API services.
 */
internal class HealthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val userApi: ProfileApiService,
    private val mediaApi: MediaApiService,
    private val messageApi: MessageApiService,
) : HealthRepository {

    override suspend fun authHealth(): ApiResult<HealthResponse> =
        authApi.health().toApiResult()

    override suspend fun userHealth(): ApiResult<HealthResponse> =
        userApi.health().toApiResult()

    override suspend fun mediaHealth(): ApiResult<HealthResponse> =
        mediaApi.health().toApiResult()

    override suspend fun messageHealth(): ApiResult<HealthResponse> =
        messageApi.health().toApiResult()

    override suspend fun allHealth(): Map<String, ApiResult<HealthResponse>> = coroutineScope {
        val auth = async { authHealth() }
        val user = async { userHealth() }
        val media = async { mediaHealth() }
        val message = async { messageHealth() }

        mapOf(
            SERVICE_AUTH to auth.await(),
            SERVICE_USER to user.await(),
            SERVICE_MEDIA to media.await(),
            SERVICE_MESSAGE to message.await(),
        )
    }

    internal companion object {
        const val SERVICE_AUTH = "auth"
        const val SERVICE_USER = "user"
        const val SERVICE_MEDIA = "media"
        const val SERVICE_MESSAGE = "message"
    }
}
