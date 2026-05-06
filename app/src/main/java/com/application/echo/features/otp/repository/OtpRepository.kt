package com.application.echo.features.otp.repository

import android.app.Activity
import com.application.echo.features.auth.model.AuthResult
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.otp.model.OtpAuthEvent
import com.application.echo.features.otp.model.OtpState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface OtpRepository {
    val otpStateFlow: StateFlow<OtpState>
    val authEventFlow: SharedFlow<OtpAuthEvent>

    val cachedPhoneInfo: PhoneInfo?
    suspend fun sendOtp(phoneInfo: PhoneInfo, context: Activity): AuthResult<Unit>
    suspend fun resendOtp(context: Activity): AuthResult<Unit>
    suspend fun verifyOtp(otp: String): AuthResult<Unit>
    fun clearOtpSession()
}