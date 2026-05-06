package com.application.echo.features.profile.repository

import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import com.application.echo.features.profile.datasource.network.ProfileNetworkSource
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.PreRegistrationInfo
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
    private val authDiskSource: AuthDiskSource,
    private val authRepository: AuthRepository,
    defaultDispatcher: CoroutineDispatcher,
) : ProfileRepository {

    private val defaultScope = CoroutineScope(defaultDispatcher)

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                when(authState) {
                    is AuthState.CreateProfile -> diskSource.startCreatingProfile(authState.phoneInfo.phoneNumber)
                    else -> {}
                }
            }
            .launchIn(defaultScope)

        combine(
            diskSource.creatingProfileStateFlow,
            diskSource.creatingProfileDisplayNameStateFlow,
            diskSource.creatingProfileFirstNameStateFlow,
            diskSource.creatingProfileLastNameStateFlow,
            diskSource.creatingProfileDateOfBirthStateFlow,
            diskSource.creatingProfileGenderStateFlow,
            authDiskSource.registerEmailStateFlow,
            authDiskSource.registerPasswordStateFlow,
            authDiskSource.registerPhoneNumberStateFlow,
        ) { values ->
            Timber.i("Combining creating profile state with values: $values")
            val isCreatingProfile = values[0] as Boolean
            val displayName = values[1] as String?
            val firstName = values[2] as String?
            val lastName = values[3] as String?
            val dateOfBirth = values[4] as Long?
            val gender = values[5] as String?
            val email = values[6] as String?
            val password = values[7] as String?
            val phoneNumber = values[8] as String?
            when {
                isCreatingProfile -> {
                    CreatingProfileState.Creating(
                        state = ProfileState(
                            displayName = displayName,
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                        ),
                        preRegistrationInfo = PreRegistrationInfo(
                            email = email,
                            password = password,
                            phoneNumber = phoneNumber,
                        ),
                    )
                }
                else -> CreatingProfileState.Started
            }
        }.distinctUntilChanged().onEach { state ->
            Timber.i("Creating Profile State: $state")
            _creatingProfileStateFlow.value = state
        }.launchIn(defaultScope)
    }

    override val profileStateFlow: Flow<ProfileState>
        get() = diskSource.profileStateFlow

    private val _creatingProfileStateFlow = MutableStateFlow<CreatingProfileState>(CreatingProfileState.Started)

    override val creatingProfileStateFlow: Flow<CreatingProfileState>
        get() = _creatingProfileStateFlow.asStateFlow()


    override suspend fun checkUsernameAvailable(username: String): Boolean {
        val response = networkSource.checkUserNameAvailability(username)
        return response.fold(
            onSuccess = { !it.exists },
            onError = {
                false
            }
        )
    }

    override fun saveProfile(
        displayName: String?,
        firstName: String?,
        lastName: String?,
        dateOfBirth: Long?,
        gender: String?
    ): ProfileResult<Unit> {
        displayName?.let { diskSource.creatingProfileDisplayName = it }
        firstName?.let { diskSource.creatingProfileFirstName = it }
        lastName?.let { diskSource.creatingProfileLastName = it }
        dateOfBirth?.let { diskSource.creatingProfileDateOfBirth = it }
        gender?.let { diskSource.creatingProfileGender = it }
        return ProfileResult.Success(Unit)
    }

}