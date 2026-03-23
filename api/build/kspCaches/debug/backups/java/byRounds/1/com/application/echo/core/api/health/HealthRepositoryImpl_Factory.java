package com.application.echo.core.api.health;

import com.application.echo.core.api.auth.AuthApiService;
import com.application.echo.core.api.media.MediaApiService;
import com.application.echo.core.api.message.MessageApiService;
import com.application.echo.core.api.user.UserApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class HealthRepositoryImpl_Factory implements Factory<HealthRepositoryImpl> {
  private final Provider<AuthApiService> authApiProvider;

  private final Provider<UserApiService> userApiProvider;

  private final Provider<MediaApiService> mediaApiProvider;

  private final Provider<MessageApiService> messageApiProvider;

  public HealthRepositoryImpl_Factory(Provider<AuthApiService> authApiProvider,
      Provider<UserApiService> userApiProvider, Provider<MediaApiService> mediaApiProvider,
      Provider<MessageApiService> messageApiProvider) {
    this.authApiProvider = authApiProvider;
    this.userApiProvider = userApiProvider;
    this.mediaApiProvider = mediaApiProvider;
    this.messageApiProvider = messageApiProvider;
  }

  @Override
  public HealthRepositoryImpl get() {
    return newInstance(authApiProvider.get(), userApiProvider.get(), mediaApiProvider.get(), messageApiProvider.get());
  }

  public static HealthRepositoryImpl_Factory create(Provider<AuthApiService> authApiProvider,
      Provider<UserApiService> userApiProvider, Provider<MediaApiService> mediaApiProvider,
      Provider<MessageApiService> messageApiProvider) {
    return new HealthRepositoryImpl_Factory(authApiProvider, userApiProvider, mediaApiProvider, messageApiProvider);
  }

  public static HealthRepositoryImpl newInstance(AuthApiService authApi, UserApiService userApi,
      MediaApiService mediaApi, MessageApiService messageApi) {
    return new HealthRepositoryImpl(authApi, userApi, mediaApi, messageApi);
  }
}
