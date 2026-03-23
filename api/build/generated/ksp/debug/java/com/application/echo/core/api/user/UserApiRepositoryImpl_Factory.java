package com.application.echo.core.api.user;

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
public final class UserApiRepositoryImpl_Factory implements Factory<UserApiRepositoryImpl> {
  private final Provider<UserApiService> apiProvider;

  public UserApiRepositoryImpl_Factory(Provider<UserApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public UserApiRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static UserApiRepositoryImpl_Factory create(Provider<UserApiService> apiProvider) {
    return new UserApiRepositoryImpl_Factory(apiProvider);
  }

  public static UserApiRepositoryImpl newInstance(UserApiService api) {
    return new UserApiRepositoryImpl(api);
  }
}
