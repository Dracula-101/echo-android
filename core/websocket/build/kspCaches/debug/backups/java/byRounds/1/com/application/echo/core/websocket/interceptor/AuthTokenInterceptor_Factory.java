package com.application.echo.core.websocket.interceptor;

import com.google.gson.Gson;
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
public final class AuthTokenInterceptor_Factory implements Factory<AuthTokenInterceptor> {
  private final Provider<TokenProvider> tokenProvider;

  private final Provider<Gson> gsonProvider;

  public AuthTokenInterceptor_Factory(Provider<TokenProvider> tokenProvider,
      Provider<Gson> gsonProvider) {
    this.tokenProvider = tokenProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public AuthTokenInterceptor get() {
    return newInstance(tokenProvider.get(), gsonProvider.get());
  }

  public static AuthTokenInterceptor_Factory create(Provider<TokenProvider> tokenProvider,
      Provider<Gson> gsonProvider) {
    return new AuthTokenInterceptor_Factory(tokenProvider, gsonProvider);
  }

  public static AuthTokenInterceptor newInstance(TokenProvider tokenProvider, Gson gson) {
    return new AuthTokenInterceptor(tokenProvider, gson);
  }
}
