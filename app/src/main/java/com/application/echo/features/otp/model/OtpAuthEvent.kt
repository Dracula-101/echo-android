package com.application.echo.features.otp.model

import com.application.echo.api.auth.LoginResponse
import com.application.echo.features.auth.model.PhoneInfo

sealed class OtpAuthEvent {
    data class Authenticated(val response: LoginResponse) : OtpAuthEvent()
    data class CreateAccount(val phoneInfo: PhoneInfo) : OtpAuthEvent()
}