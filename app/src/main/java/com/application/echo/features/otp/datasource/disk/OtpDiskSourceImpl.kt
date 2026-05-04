package com.application.echo.features.otp.datasource.disk

import android.content.SharedPreferences
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.platform.base.BaseEncryptedDiskSource
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.features.auth.model.PhoneInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val KEY_PHONE_INFO = "otp_cached_phone_info"
private const val KEY_VERIFICATION_ID = "otp_cached_verification_id"

class OtpDiskSourceImpl @Inject constructor(
    @UnencryptedPreferences sharedPreferences: SharedPreferences,
    @EncryptedPreferences encryptedSharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseEncryptedDiskSource(
    sharedPreferences = sharedPreferences,
    encryptedSharedPreferences = encryptedSharedPreferences,
), OtpDiskSource {

    private val _cachedPhoneInfoFlow = bufferedMutableSharedFlow<PhoneInfo?>()

    override var cachedPhoneInfo: PhoneInfo?
        get() = getEncryptedString(KEY_PHONE_INFO)?.let {
            runCatching { json.decodeFromString<PhoneInfo>(it) }
                .getOrNull()
                ?.takeUnless { phone ->
                    phone.isExpired().also { expired ->
                        if (expired) remove(KEY_PHONE_INFO)
                    }
                }
        }
        set(value) {
            if (value != null) {
                putEncryptedString(KEY_PHONE_INFO, json.encodeToString(value))
            } else {
                remove(KEY_PHONE_INFO)
            }
            _cachedPhoneInfoFlow.tryEmit(value)
        }

    override val cachedPhoneInfoFlow: Flow<PhoneInfo?>
        get() = _cachedPhoneInfoFlow.onSubscription { emit(cachedPhoneInfo) }

    private val _cachedVerificationIdFlow = bufferedMutableSharedFlow<String?>()

    override var cachedVerificationId: String?
        get() = getEncryptedString(KEY_VERIFICATION_ID)
        set(value) {
            if (value != null) {
                putEncryptedString(KEY_VERIFICATION_ID, value)
            } else {
                remove(KEY_VERIFICATION_ID)
            }
            _cachedVerificationIdFlow.tryEmit(value)
        }

    override val cachedVerificationIdFlow: Flow<String?>
        get() = _cachedVerificationIdFlow.onSubscription { emit(cachedVerificationId) }
}