package com.application.echo.presentation.create_profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.net.Uri
import android.os.Parcelable
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.notification.permission.NotificationPermissionHelper
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileVisibility
import com.application.echo.features.profile.model.fold
import com.application.echo.features.profile.repository.ProfileRepository
import com.application.echo.presentation.register.PasswordStrength
import com.application.echo.presentation.register.passwordStrength
import com.application.echo.presentation.rootnav.CreateProfileScreenRoute
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
    private val notificationHelper: NotificationPermissionHelper
) : BaseViewModel<CreateProfileState, CreateProfileEvent, CreateProfileAction>(
    initialState = savedStateHandle[KEY_STATE] ?: CreateProfileState(
        userId = savedStateHandle.toRoute<CreateProfileScreenRoute>().userId
    ),
) {

    private val usernameQueryFlow = MutableStateFlow<String?>(null)

    init {
        profileRepository.creatingProfileStateFlow
            .onEach { profileState ->
                Timber.i("Received profile state: $profileState")
                if (profileState is CreatingProfileState.Creating) {
                    setState {
                        state.copy(
                            displayName = profileState.state.displayName,
                            dateOfBirth = profileState.state.dateOfBirth,
                        )
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

        viewModelScope.launch {
            setState { state.copy(allowNotifications = notificationHelper.isGranted) }
        }
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
            is CreateProfileAction.OnAvatarSelected -> setState {
                state.copy(avatarUri = action.uri)
            }
            is CreateProfileAction.UpdateCurrentPage -> setState {
                state.copy(currentPage = ProfileScreen.fromIndex(action.page))
            }
            is CreateProfileAction.OnGradientSelected -> setState {
                state.copy(selectedGradientIndex = action.index)
            }
            is CreateProfileAction.OnEmojiSelected -> setState {
                state.copy(selectedEmojiIndex = action.index)
            }
            is CreateProfileAction.OnToggleSearchable -> setState {
                state.copy(isSearchable = !state.isSearchable)
            }
            is CreateProfileAction.OnNotificationChanged -> setState {
                state.copy(allowNotifications = action.allow)
            }
            is CreateProfileAction.OnPrevious -> setState {
                when (state.currentPage) {
                    ProfileScreen.INFO -> state
                    ProfileScreen.CUSTOMIZATION -> state.copy(currentPage = ProfileScreen.INFO)
                }
            }
            is CreateProfileAction.OnNext -> setState {
                when (state.currentPage) {
                    ProfileScreen.INFO -> state.copy(currentPage = ProfileScreen.CUSTOMIZATION)
                    ProfileScreen.CUSTOMIZATION -> state
                }
            }
            CreateProfileAction.OnContinueClick -> {
                when (state.currentPage) {
                    ProfileScreen.INFO -> handleInfoContinue()
                    ProfileScreen.CUSTOMIZATION -> handleCustomizationContinue()
                }
            }
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun handleInfoContinue() {
        val displayNameError = when {
            state.displayName.isNullOrBlank() -> "Display name cannot be empty"
            state.displayName!!.length < 3 -> "Display name must be at least 3 characters"
            else -> null
        }
        val bioError = if (state.bio.isNullOrBlank()) "Bio cannot be empty" else null

        if (displayNameError != null || bioError != null) {
            setState {
                state.copy(
                    displayNameError = displayNameError,
                    bioError = bioError,
                )
            }
            return
        }

        saveUserInfo()
        setState {
            state.copy(
                displayNameError = null,
                bioError = null,
                currentPage = ProfileScreen.CUSTOMIZATION,
            )
        }
    }

    private fun handleCustomizationContinue() {
        when {
            state.userName.isNullOrBlank() -> {
                setState { state.copy(userNameError = "Username cannot be empty") }
            }
            // Still waiting on the debounced check — don't block, just wait.
            state.isValidatingUserName -> {
                setState { state.copy(userNameError = "Checking username availability…") }
            }
            !state.isValidUserName -> {
                setState { state.copy(userNameError = "Username is not available") }
            }
            else -> registerProfile()
        }
    }

    private fun saveUserInfo() {
        val parts = state.displayName.orEmpty().trim().split(" ", limit = 2)
        profileRepository.saveProfile(
            displayName = state.displayName,
            firstName = parts.getOrElse(0) { "" },
            lastName = parts.getOrElse(1) { "" },
            dateOfBirth = state.dateOfBirth,
        )
    }

    private fun registerProfile() {
        viewModelScope.launch {
            setState { state.copy(isLoading = true) }
            // silent login to get access tokens
            authRepository.silentLogin()
            val firstName = state.displayName.orEmpty().trim().split(" ", limit = 2).getOrElse(0) { "" }
            val lastName = state.displayName.orEmpty().trim().split(" ", limit = 2).getOrElse(1) { "" }
            val result = profileRepository.createProfile(
                userName = state.userName!!,
                userId = state.userId,
                displayName = state.displayName!!,
                firstName = firstName,
                lastName = lastName,
                bio = state.bio.orEmpty(),
                profileVisibility = ProfileVisibility.PUBLIC,
                searchable = state.isSearchable,
                pushEnabled = state.allowNotifications && !state.skipNotification,
            )
            result.fold(
                onSuccess = {
                    setState { state.copy(isLoading = false) }
                },
                onError = { error ->
                    setState { state.copy(isLoading = false) }
                    sendEvent(
                        CreateProfileEvent.ShowSnackbar(
                            message = "Profile creation failed",
                            detail = error.message,
                            code = error.code,
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

val profileEmojis = listOf( "✨", "🌙", "🖤", "🔥", "💫", "🌊")

val avatarGradientStops: List<Pair<Color, Color>> = listOf(
    Color(0xFFFF5E5E) to Color(0xFFFFAB91),  // Coral
    Color(0xFFB8A1FF) to Color(0xFFFF7878),  // Violet → Coral
    Color(0xFF7FE8C4) to Color(0xFF8BC4FF),  // Mint → Sky
    Color(0xFFFFD28B) to Color(0xFFFF7A3D),  // Apricot
    Color(0xFF8BC4FF) to Color(0xFFB8A1FF),  // Sky → Violet
    Color(0xFFFF7878) to Color(0xFFE94F8E),  // Pink
)

enum class ProfileScreen(val index: Int) {
    INFO(1),
    CUSTOMIZATION(2);

    companion object {
        fun fromIndex(index: Int): ProfileScreen {
            return entries.firstOrNull { it.index == index } ?: INFO
        }
    }
}

@Parcelize
data class CreateProfileState(
    val userId: String,
    val currentPage: ProfileScreen = ProfileScreen.INFO,
    val dateOfBirth: Long? = null,
    val displayName: String? = null,
    val displayNameError: String? = null,
    val userName: String? = null,
    val userNameError: String? = null,
    val isValidatingUserName: Boolean = false,
    val isValidUserName: Boolean = false,
    val avatarUri: Uri? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val bioError: String? = null,
    val selectedGradientIndex: Int = 0,
    val selectedEmojiIndex: Int = 0,
    val isSearchable: Boolean = true,
    val allowNotifications: Boolean = false,
    val skipNotification: Boolean = false,
    val isLoading: Boolean = false,
) : Parcelable

sealed interface CreateProfileEvent {
    data class ShowSnackbar(val message: String, val detail: String, val code: String): CreateProfileEvent
}

sealed interface CreateProfileAction {
    data class OnDisplayNameChanged(val value: String) : CreateProfileAction
    data class OnDateOfBirthChanged(val value: Long) : CreateProfileAction
    data class OnUserNameChanged(val value: String) : CreateProfileAction
    data class OnBioChanged(val value: String) : CreateProfileAction
    data class OnAvatarSelected(val uri: Uri) : CreateProfileAction
    data class UpdateCurrentPage(val page: Int) : CreateProfileAction
    data class OnGradientSelected(val index: Int) : CreateProfileAction
    data class OnEmojiSelected(val index: Int) : CreateProfileAction
    data object OnToggleSearchable : CreateProfileAction
    data class OnNotificationChanged(val allow: Boolean) : CreateProfileAction
    data object OnPrevious: CreateProfileAction
    data object OnNext: CreateProfileAction
    data object OnContinueClick : CreateProfileAction
}