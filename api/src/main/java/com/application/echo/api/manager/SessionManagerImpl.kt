package com.application.echo.api.manager

import android.content.SharedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.platform.base.BaseDiskSource
import javax.inject.Inject

private const val SESSION_ID_KEY = "session_id"
private const val SESSION_TOKEN_KEY = "session_token"

class SessionManagerImpl @Inject constructor(
    @UnencryptedPreferences sharedPreferences: SharedPreferences,
) : BaseDiskSource(sharedPreferences), SessionManager {

    override val sessionId: String?
        get() = getString(SESSION_ID_KEY)

    override val sessionToken: String?
        get() = getString(SESSION_TOKEN_KEY)

    override fun storeSession(sessionId: String, sessionToken: String) {
        putString(SESSION_ID_KEY, sessionId)
        putString(SESSION_TOKEN_KEY, sessionToken)
    }

    override fun clearSession() {
        remove(SESSION_ID_KEY)
        remove(SESSION_TOKEN_KEY)
    }
}
