package com.application.echo.features.auth.model

data class OtpState(
    val phoneInfo: PhoneInfo?,
    val state: OtpVerificationState,
)

sealed class OtpVerificationState {
    object Idle : OtpVerificationState()
    object Sending : OtpVerificationState()
    object Sent : OtpVerificationState()
    data class Failed(val error: String) : OtpVerificationState()
    data object Verifying : OtpVerificationState()
    data object Success : OtpVerificationState()
}


