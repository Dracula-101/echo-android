package com.application.echo.presentation.create_profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.net.Uri
import android.os.Parcelable
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.notification.token.FcmTokenManager
import com.application.echo.features.otp.datasource.disk.OtpDiskSource
import com.application.echo.features.otp.model.OtpAuthEvent
import com.application.echo.features.otp.repository.OtpRepository
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.fold
import com.application.echo.features.profile.repository.ProfileRepository
import com.application.echo.ui.components.picker.EchoDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : BaseViewModel<CreateProfileState, CreateProfileEvent, CreateProfileAction>(
    initialState = savedStateHandle[KEY_STATE] ?: CreateProfileState(),
) {

    private val usernameQueryFlow = MutableStateFlow<String?>(null)

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                if (authState is AuthState.CreateProfile) {
                    setState {
                        state.copy(
                            phoneInfo = authState.phoneInfo,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
        profileRepository.creatingProfileStateFlow
            .onEach { profileState ->
                Timber.i("Received profile state: $profileState")
                if (profileState is CreatingProfileState.Creating) {
                    setState {
                        state.copy(
                            displayName = profileState.state.displayName,
                            gender = profileState.state.gender?.let { UserGender.parse(it) },
                            dateOfBirth = profileState.state.dateOfBirth,
                            email = profileState.preRegistrationInfo.email,
                            password = profileState.preRegistrationInfo.password,
                        )
                    }
                    // Trigger username validation if we already have a username (e.g. user is returning to the app during profile creation)
                    if (state.email != null) {
                        val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(state.email!!).matches()
                        setState {
                            state.copy(
                                isValidEmail = isValidEmail,
                                emailError = if (!isValidEmail) "Invalid email address" else null
                            )
                        }
                    }
                    if (state.password != null) {
                        val passwordStrength = state.password!!.passwordStrength()
                        setState {
                            state.copy(
                                passwordStrength = passwordStrength,
                                passwordError = when (passwordStrength) {
                                    PasswordStrength.Empty -> "Password cannot be empty"
                                    PasswordStrength.Weak -> "Password is too weak"
                                    else -> null
                                }
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
        usernameQueryFlow
            .filterNotNull()
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { username -> validateUsername(username) }
            .launchIn(viewModelScope)
    }

    private fun validateUsername(username: String) {
        viewModelScope.launch {
            setState { state.copy(isValidatingUserName = true) }
            val isAvailable = profileRepository.checkUsernameAvailable(username)
            setState {
                state.copy(
                    isValidatingUserName = false,
                    isValidUserName = isAvailable,
                    userNameError = if (isAvailable) null else "Username is already taken"
                )
            }
        }
    }


    override fun handleAction(action: CreateProfileAction) {
        when (action) {
            is CreateProfileAction.OnEmailChanged -> setState {
                state.copy(
                    email = action.value,
                    isValidEmail = Patterns.EMAIL_ADDRESS.matcher(action.value).matches()
                )
            }
            is CreateProfileAction.OnPasswordChanged -> setState {
                state.copy(
                    password = action.value,
                    passwordStrength = action.value.passwordStrength(),
                )
            }
            is CreateProfileAction.OnChangePasswordVisibility -> setState {
                state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
            is CreateProfileAction.OnDisplayNameChanged -> setState {
                state.copy(displayName = action.value)
            }
            is CreateProfileAction.OnBioChanged -> setState {
                state.copy(bio = action.value)
            }
            is CreateProfileAction.OnUserNameChanged -> {
                val sanitized = action.value
                    .lowercase()
                    .filter { it.isLetterOrDigit() || it == '_' || it == '.' }

                val changed = sanitized != state.userName

                setState {
                    state.copy(
                        userName = sanitized,
                        isValidatingUserName = changed && sanitized.isNotBlank(),
                        isValidUserName = if (changed) false else state.isValidUserName,
                        userNameError = if (changed) null else state.userNameError,
                    )
                }

                if (changed) {
                    usernameQueryFlow.value = sanitized.takeIf { it.isNotBlank() }
                }
            }
            is CreateProfileAction.OnDateOfBirthChanged -> setState {
                state.copy(dateOfBirth = action.value)
            }
            is CreateProfileAction.OnGenderChanged -> setState {
                state.copy(gender = action.value)
            }
            is CreateProfileAction.OnAvatarSelected -> setState {
                state.copy(avatarUri = action.uri)
            }
            is CreateProfileAction.UpdateCurrentPage -> setState {
                state.copy(currentPage = action.page)
            }
            is CreateProfileAction.OnGradientSelected -> setState {
                state.copy(selectedGradientIndex = action.index)
            }
            is CreateProfileAction.OnEmojiSelected -> setState {
                state.copy(selectedEmojiIndex = action.index)
            }
            is CreateProfileAction.OnPrevious -> setState {
                state.copy(currentPage = (state.currentPage - 1).coerceAtLeast(0))
            }
            is CreateProfileAction.OnNext -> setState {
                state.copy(currentPage = (state.currentPage + 1).coerceAtMost(2))
            }
            CreateProfileAction.OnContinueClick -> {
                when(state.currentPage) {
                    1 -> {
                        if (!state.isValidEmail || state.password == null || state.passwordStrength == PasswordStrength.Weak) {
                            setState {
                                state.copy(
                                    emailError = if (!state.isValidEmail) "Invalid email address" else null,
                                    passwordError = when {
                                        state.password == null -> "Password cannot be empty"
                                        state.passwordStrength == PasswordStrength.Weak -> "Password is too weak"
                                        else -> null
                                    },
                                    currentPage = 1
                                )
                            }
                        } else {
                            setState {
                                state.copy(
                                    emailError = null,
                                    passwordError = null,
                                    currentPage = 2
                                )
                            }
                            savePreRegistrationInfo()
                        }
                    }
                    2 -> {
                        if (state.displayName.isNullOrBlank() || state.displayName!!.length < 3 || state.bio.isNullOrBlank()) {
                            setState {
                                state.copy(
                                    displayNameError = when {
                                        state.displayName.isNullOrBlank() -> "Display name cannot be empty"
                                        state.displayName!!.length < 3 -> "Display name must be at least 3 characters"
                                        else -> null
                                    },
                                    bioError = if (state.bio.isNullOrBlank()) "Bio cannot be empty" else null,
                                    currentPage = 2
                                )
                            }
                        } else {
                            setState {
                                state.copy(
                                    displayNameError = null,
                                    bioError = null,
                                    currentPage = 3
                                )
                            }
                            saveUserInfo()
                        }
                    }
                    3 -> {
                        if (state.userName.isNullOrBlank() || !state.isValidUserName) {
                            setState {
                                state.copy(
                                    userNameError = when {
                                        state.userName.isNullOrBlank() -> "Username cannot be empty"
                                        !state.isValidUserName -> "Username is not available"
                                        else -> null
                                    },
                                    currentPage = 3
                                )
                            }
                        } else {
                            registerProfile()
                        }
                    }
                }
            }
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun savePreRegistrationInfo() {
        authRepository.setPreRegistrationInfo(
            email = state.email!!,
            password = state.password!!,
            phoneNumber = state.phoneInfo!!.phoneNumber,
        )
    }

    private fun saveUserInfo() {
        val parts = state.displayName.orEmpty().trim().split(" ", limit = 2)
        val firstName = parts.getOrElse(0) { "" }
        val lastName = parts.getOrElse(1) { "" }
        profileRepository.saveProfile(
            displayName = state.displayName,
            firstName = firstName,
            lastName = lastName,
            gender = state.gender.toString(),
            dateOfBirth = state.dateOfBirth,
        )
    }

    private fun registerProfile() {
        viewModelScope.launch {
            setState { state.copy(isLoading = true) }
            val registerResult = authRepository.register(
                email = state.email!!,
                password = state.password!!,
                phoneNumber = state.phoneInfo!!.phoneNumber,
                phoneCountryCode = state.phoneInfo!!.country.isoCode,
                acceptTerms = true,
            )
            registerResult.fold(
                onSuccess = { user ->
                },
                onError = { error ->
                    setState { state.copy(isLoading = false) }
                    sendEvent(
                        CreateProfileEvent.ShowSnackbar(
                            message = "Registration failed",
                            detail = error.message,
                            code = error.code
                        )
                    )
                }
            )
        }
    }



    companion object {
        private const val KEY_STATE = "create_profile_state"
    }
}

enum class UserGender {
    WOMAN,
    MAN,
    NON_BINARY,
    OTHER,
    PREFER_NOT_TO_SAY;

    override fun toString(): String {
        return when (this) {
            WOMAN -> "Woman"
            MAN -> "Man"
            NON_BINARY -> "Non-binary"
            OTHER -> "Other"
            PREFER_NOT_TO_SAY -> "Prefer not to say"
        }
    }

    companion object {
        fun parse(value: String): UserGender {
            return when (value) {
                "Man" -> MAN
                "Woman" -> WOMAN
                "Non-binary" -> NON_BINARY
                "Other" -> OTHER
                "Prefer not to say" -> PREFER_NOT_TO_SAY
                else -> OTHER
            }
        }
    }
}

val profileEmojis = listOf( "✨", "🌙", "🖤", "🔥", "💫", "🌊")

val avatarGradientStops: List<Pair<Color, Color>> = listOf(
    Color(0xFFFF5E5E) to Color(0xFFFFAB91),  // Coral
    Color(0xFFB8A1FF) to Color(0xFFFF7878),  // Violet → Coral
    Color(0xFF7FE8C4) to Color(0xFF8BC4FF),  // Mint → Sky
    Color(0xFFFFD28B) to Color(0xFFFF7A3D),  // Apricot
    Color(0xFF8BC4FF) to Color(0xFFB8A1FF),  // Sky → Violet
    Color(0xFFFF7878) to Color(0xFFE94F8E),  // Pink
)

@Parcelize
data class CreateProfileState(
    val currentPage: Int = 1,
    val phoneInfo: PhoneInfo? = null,
    val email: String? = null,
    val emailError: String? = null,
    val isValidEmail: Boolean = false,
    val password: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.Empty,
    val dateOfBirth: Long? = null,
    val displayName: String? = null,
    val displayNameError: String? = null,
    val userName: String? = null,
    val userNameError: String? = null,
    val isValidatingUserName: Boolean = false,
    val isValidUserName: Boolean = false,
    val gender: UserGender? = null,
    val avatarUri: Uri? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val bioError: String? = null,
    val selectedGradientIndex: Int = 0,
    val selectedEmojiIndex: Int = 0,
    val isLoading: Boolean = false,
) : Parcelable

sealed interface CreateProfileEvent {
    data class ShowSnackbar(val message: String, val detail: String, val code: String): CreateProfileEvent
}

sealed interface CreateProfileAction {
    data class OnEmailChanged(val value: String) : CreateProfileAction
    data class OnPasswordChanged(val value: String) : CreateProfileAction
    data object OnChangePasswordVisibility : CreateProfileAction
    data class OnDisplayNameChanged(val value: String) : CreateProfileAction
    data class OnDateOfBirthChanged(val value: Long) : CreateProfileAction
    data class OnGenderChanged(val value: UserGender) : CreateProfileAction
    data class OnUserNameChanged(val value: String) : CreateProfileAction
    data class OnBioChanged(val value: String) : CreateProfileAction
    data class OnAvatarSelected(val uri: Uri) : CreateProfileAction
    data class UpdateCurrentPage(val page: Int) : CreateProfileAction
    data class OnGradientSelected(val index: Int) : CreateProfileAction
    data class OnEmojiSelected(val index: Int) : CreateProfileAction
    data object OnPrevious: CreateProfileAction
    data object OnNext: CreateProfileAction
    data object OnContinueClick : CreateProfileAction
}