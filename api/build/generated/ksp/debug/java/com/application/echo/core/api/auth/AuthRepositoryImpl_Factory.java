package com.application.echo.core.api.auth;

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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<AuthApiService> apiProvider;

  public AuthRepositoryImpl_Factory(Provider<AuthApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<AuthApiService> apiProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider);
  }

  public static AuthRepositoryImpl newInstance(AuthApiService api) {
    return new AuthRepositoryImpl(api);
  }
}
