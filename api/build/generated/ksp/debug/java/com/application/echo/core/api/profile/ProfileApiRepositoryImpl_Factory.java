package com.application.echo.core.api.profile;

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
public final class ProfileApiRepositoryImpl_Factory implements Factory<ProfileApiRepositoryImpl> {
  private final Provider<ProfileApiService> apiProvider;

  public ProfileApiRepositoryImpl_Factory(Provider<ProfileApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ProfileApiRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static ProfileApiRepositoryImpl_Factory create(Provider<ProfileApiService> apiProvider) {
    return new ProfileApiRepositoryImpl_Factory(apiProvider);
  }

  public static ProfileApiRepositoryImpl newInstance(ProfileApiService api) {
    return new ProfileApiRepositoryImpl(api);
  }
}
