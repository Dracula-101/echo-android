package com.application.echo.features.otp.model

import com.application.echo.features.auth.model.PhoneInfo

data class OtpState(
    val phoneInfo: PhoneInfo?,
    val state: OtpVerificationState,
    val canResend: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val resendAttempts: Int = 0,
)