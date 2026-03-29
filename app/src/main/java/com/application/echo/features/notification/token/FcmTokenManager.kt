package com.application.echo.features.notification.token

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manages the FCM registration token lifecycle.
 *
 * Responsibilities:
 * - Fetches the current token from Firebase
 * - Persists it locally so it can be attached to login/register requests
 * - Exposes a [StateFlow] so other components can react to token changes
 * - Tracks whether the token has been synced with the backend
 */
@Singleton
class FcmTokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:UnencryptedPreferences private val prefs: SharedPreferences,
) {

    private val _tokenFlow = MutableStateFlow(storedToken)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    val currentToken: String? get() = _tokenFlow.value

    /**
     * Whether the currently stored token has been sent to the backend.
     * Reset to false whenever the token changes.
     */
    var isSynced: Boolean
        get() = prefs.getBoolean(KEY_TOKEN_SYNCED, false)
        private set(value) = prefs.edit { putBoolean(KEY_TOKEN_SYNCED, value) }

    private var storedToken: String?
        get() = prefs.getString(KEY_FCM_TOKEN, null)
        set(value) {
            prefs.edit { putString(KEY_FCM_TOKEN, value) }
            _tokenFlow.value = value
        }

    /**
     * Called from [com.google.firebase.messaging.FirebaseMessagingService.onNewToken].
     * Stores the new token and marks it as not yet synced.
     */
    fun onNewToken(token: String) {
        Timber.d("FCM token updated: %s", token)
        storedToken = token
        isSynced = false
    }

    /**
     * Marks the current token as successfully sent to the backend.
     */
    fun markSynced() {
        isSynced = true
    }

    /**
     * Fetches the current FCM token from Firebase.
     * Returns null if Firebase is not initialized.
     */
    suspend fun fetchToken(): String? {
        if (FirebaseApp.getApps(context).isEmpty()) {
            Timber.w("Firebase not initialized — cannot fetch FCM token")
            return storedToken
        }

        return try {
            val token = suspendCancellableCoroutine { cont ->
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            storedToken = token
            Timber.d("FCM token fetched: %s", token)
            token
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch FCM token")
            storedToken
        }
    }

    /**
     * Clears stored token on logout.
     */
    fun clear() {
        storedToken = null
        isSynced = false
    }

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_TOKEN_SYNCED = "fcm_token_synced"
    }
}
