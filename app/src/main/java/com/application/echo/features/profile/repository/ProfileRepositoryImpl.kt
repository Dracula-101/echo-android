package com.application.echo.features.profile.repository

import android.net.Uri
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import com.application.echo.features.profile.datasource.network.ProfileNetworkSource
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileResult
import com.application.echo.features.profile.model.ProfileState
import com.application.echo.features.profile.model.fold
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val diskSource: ProfileDiskSource,
    private val networkSource: ProfileNetworkSource,
    private val authRepository: AuthRepository,
    defaultDispatcher: CoroutineDispatcher,
) : ProfileRepository {

    private val defaultScope = CoroutineScope(defaultDispatcher)

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                when(authState) {
                    is AuthState.CreateProfile -> diskSource.startCreatingProfile(authState.phoneNumber)
                    else -> {}
                }
            }
            .launchIn(defaultScope)

        combine(
            diskSource.creatingProfileStateFlow,
            diskSource.creatingProfilePhoneNumberStateFlow,
            diskSource.creatingProfileDisplayNameStateFlow,
            diskSource.creatingProfileFirstNameStateFlow,
            diskSource.creatingProfileLastNameStateFlow,
        ) { isCreatingProfile, userId, displayName, firstName, lastName ->
            when {
                isCreatingProfile && userId != null -> {
                    CreatingProfileState.Creating(
                        userId = userId,
                        firstName = firstName,
                        lastName = lastName,
                        displayName = displayName,
                    )
                }
                else -> CreatingProfileState.None
            }
        }.distinctUntilChanged().onEach { state ->
            Timber.i("Creating Profile State: $state")
            _creatingProfileStateFlow.value = state
        }.launchIn(defaultScope)
    }

    override val profileStateFlow: Flow<ProfileState>
        get() = diskSource.profileStateFlow

    private val _creatingProfileStateFlow = MutableStateFlow<CreatingProfileState>(CreatingProfileState.None)

    override val creatingProfileStateFlow: Flow<CreatingProfileState>
        get() = _creatingProfileStateFlow.asStateFlow()


    override suspend fun setProfileInfo(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUri: Uri,
    ) : ProfileResult<Unit> {
        diskSource.apply {
            creatingProfileDisplayName = displayName
            creatingProfileFirstName = firstName
            creatingProfileLastName = lastName
        }

        val profileResult = networkSource.createProfile(
            userId = userId,
            displayName = displayName,
            firstName = firstName,
            lastName = lastName,
        )

        return profileResult.fold(
            onSuccess = {
                val avatarData = networkSource.uploadAvatar(avatarUri)
                avatarData.fold(
                    onSuccess = { avatarUrl ->
                        diskSource.creatingProfileAvatarUrl = avatarUrl
                        diskSource.finishCreatingProfile()
                        authRepository.autoLogin()
                        ProfileResult.Success(Unit)
                    },
                    onError = { error ->
                        Timber.e("Failed to upload avatar: $error")
                        ProfileResult.Error(error)
                    }
                )
            },
            onError = {
                ProfileResult.Error(it)
            }
        )
    }


}