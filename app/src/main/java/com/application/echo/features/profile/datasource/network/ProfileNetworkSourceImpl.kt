package com.application.echo.features.profile.datasource.network

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.application.echo.api.media.MediaApiRepository
import com.application.echo.api.profile.CheckUsernameResponse
import com.application.echo.api.profile.CreateProfileRequest
import com.application.echo.api.profile.CreateProfileResponse
import com.application.echo.api.profile.GetProfileResponse
import com.application.echo.api.profile.ProfileApiRepository
import com.application.echo.core.common.platform.util.MimeTypeResolver
import com.application.echo.core.common.platform.util.toFile
import com.application.echo.features.profile.model.ProfileResult
import com.application.echo.features.profile.model.ProfileVisibility
import com.application.echo.features.profile.model.map
import com.application.echo.features.profile.model.onSuccess
import com.application.echo.features.profile.model.toProfileResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ProfileNetworkSourceImpl @Inject constructor(
    private val profileApi: ProfileApiRepository,
    private val mediaApi: MediaApiRepository,
    private val mimeTypeResolver: MimeTypeResolver,
    @param:ApplicationContext private val context: Context,
) : ProfileNetworkSource {

    override suspend fun checkUserNameAvailability(username: String): ProfileResult<CheckUsernameResponse> {
        return profileApi
            .checkUsernameAvailability(username)
            .toProfileResult()
    }

    override suspend fun getProfile(userId: String): ProfileResult<GetProfileResponse> {
        return profileApi
            .getProfile(userId)
            .toProfileResult()
    }

    override suspend fun createProfile(
        userId: String,
        displayName: String,
        userName: String,
        firstName: String,
        lastName: String,
        bio: String,
        profileVisibility: ProfileVisibility,
        searchable: Boolean,
        pushEnabled: Boolean
    ): ProfileResult<CreateProfileResponse> = profileApi.createProfile(
        userId = userId,
        displayName = displayName,
        userName = userName,
        firstName = firstName,
        lastName = lastName,
        bio = bio,
        profileVisibility = profileVisibility.toString(),
        searchable = searchable,
        pushEnabled = pushEnabled,
     ).toProfileResult()

    override suspend fun uploadAvatar(uri: Uri): ProfileResult<String> {
        val profilePhotoFile = uri.toFile(context = context)
        val mimeType = mimeTypeResolver.resolveDetailed(uri)
        return mediaApi
            .uploadProfilePhoto(profilePhotoFile, mimeType.mimeType)
            .toProfileResult()
            .map { it.storageUrl }
    }
}