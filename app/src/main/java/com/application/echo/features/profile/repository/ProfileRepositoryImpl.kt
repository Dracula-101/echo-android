package com.application.echo.features.profile.repository

import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import com.application.echo.features.profile.datasource.network.ProfileNetworkSource
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val diskSource: ProfileDiskSource,
    private val networkSource: ProfileNetworkSource,
    authRepository: AuthRepository,
    defaultDispatcher: CoroutineDispatcher,
) : ProfileRepository {

    private val defaultScope = CoroutineScope(defaultDispatcher)

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                when(authState) {
                    is AuthState.Authenticated -> {
                        if (diskSource.creatingProfileState) diskSource.finishCreatingProfile(authState.user.userId)
                    }
                    // first time call from register
                    is AuthState.CreateProfile -> {
                        diskSource.startCreatingProfile(authState.userId)
                    }
                    is AuthState.Unauthenticated, AuthState.Initializing -> {}
                }
            }
            .launchIn(defaultScope)

        combine(
            diskSource.creatingProfileStateFlow,
            diskSource.creatingProfileUserIdStateFlow,
        ) { isCreatingProfile, userId ->
            when {
                isCreatingProfile && userId != null -> {
                    CreatingProfileState.Creating(userId)
                }
                else -> CreatingProfileState.None
            }
        }.onEach { state ->
            _creatingProfileStateFlow.value = state
        }.launchIn(defaultScope)
    }

    override val profileStateFlow: Flow<ProfileState>
        get() = diskSource.profileStateFlow

    private val _creatingProfileStateFlow = MutableStateFlow<CreatingProfileState>(CreatingProfileState.None)

    override val creatingProfileStateFlow: Flow<CreatingProfileState>
        get() = _creatingProfileStateFlow.asStateFlow()

}