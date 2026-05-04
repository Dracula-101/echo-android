package com.application.echo.features.otp.datasource.disk

import com.application.echo.features.auth.model.PhoneInfo
import kotlinx.coroutines.flow.Flow

interface OtpDiskSource {
    var cachedPhoneInfo: PhoneInfo?
    val cachedPhoneInfoFlow: Flow<PhoneInfo?>

    var cachedVerificationId: String?
    val cachedVerificationIdFlow: Flow<String?>
}