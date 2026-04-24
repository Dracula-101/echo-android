package com.application.echo.api.manager

import com.application.echo.core.network.provider.SessionProvider

interface SessionManager : SessionProvider {

    fun storeSession(sessionId: String, sessionToken: String)

    fun clearSession()
}