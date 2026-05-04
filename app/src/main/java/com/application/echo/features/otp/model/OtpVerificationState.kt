package com.application.echo.features.otp.model

sealed class OtpVerificationState {
    object Idle : OtpVerificationState()
    object Sending : OtpVerificationState()
    object Sent : OtpVerificationState()
    data object Verifying : OtpVerificationState()
    data object Success : OtpVerificationState()
    data class Failed(val error: String) : OtpVerificationState()
}