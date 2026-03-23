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
public final class TokenRefreshAuthenticator_Factory implements Factory<TokenRefreshAuthenticator> {
  private final Provider<AuthApiService> authApiProvider;

  private final Provider<TokenRefreshListener> listenerProvider;

  public TokenRefreshAuthenticator_Factory(Provider<AuthApiService> authApiProvider,
      Provider<TokenRefreshListener> listenerProvider) {
    this.authApiProvider = authApiProvider;
    this.listenerProvider = listenerProvider;
  }

  @Override
  public TokenRefreshAuthenticator get() {
    return newInstance(authApiProvider.get(), listenerProvider.get());
  }

  public static TokenRefreshAuthenticator_Factory create(Provider<AuthApiService> authApiProvider,
      Provider<TokenRefreshListener> listenerProvider) {
    return new TokenRefreshAuthenticator_Factory(authApiProvider, listenerProvider);
  }

  public static TokenRefreshAuthenticator newInstance(AuthApiService authApi,
      TokenRefreshListener listener) {
    return new TokenRefreshAuthenticator(authApi, listener);
  }
}
