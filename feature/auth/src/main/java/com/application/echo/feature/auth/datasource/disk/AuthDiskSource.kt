package com.application.echo.feature.auth.datasource.disk

import com.application.echo.feature.auth.model.UserState
import kotlinx.coroutines.flow.Flow

interface AuthDiskSource {

    var userState: UserState

    val userStateFlow: Flow<UserState>


}