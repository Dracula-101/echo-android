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
public final class AuthApiRepositoryImpl_Factory implements Factory<AuthApiRepositoryImpl> {
  private final Provider<AuthApiService> apiProvider;

  public AuthApiRepositoryImpl_Factory(Provider<AuthApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public AuthApiRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static AuthApiRepositoryImpl_Factory create(Provider<AuthApiService> apiProvider) {
    return new AuthApiRepositoryImpl_Factory(apiProvider);
  }

  public static AuthApiRepositoryImpl newInstance(AuthApiService api) {
    return new AuthApiRepositoryImpl(api);
  }
}
