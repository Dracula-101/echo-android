package com.application.echo.core.api.health

import com.application.echo.core.api.common.HealthResponse
import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for health-check operations across all backend services.
 *
 * Useful for connection diagnostics, splash-screen readiness checks,
 * or a debug/settings health dashboard.
 */
interface HealthRepository {

    /** Check the Auth service. */
    suspend fun authHealth(): ApiResult<HealthResponse>

    /** Check the User service. */
    suspend fun userHealth(): ApiResult<HealthResponse>

    /** Check the Media service. */
    suspend fun mediaHealth(): ApiResult<HealthResponse>

    /** Check the Messages service. */
    suspend fun messageHealth(): ApiResult<HealthResponse>

    /**
     * Check all backend services in parallel.
     *
     * Returns a map of service name → result. Failures for individual
     * services do not prevent the others from being checked.
     */
    suspend fun allHealth(): Map<String, ApiResult<HealthResponse>>
}
