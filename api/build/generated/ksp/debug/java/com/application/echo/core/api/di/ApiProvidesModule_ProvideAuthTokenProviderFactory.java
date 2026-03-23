package com.application.echo.core.api.di;

import com.application.echo.core.api.manager.AuthTokenManager;
import com.application.echo.core.network.interceptor.AuthTokenProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ApiProvidesModule_ProvideAuthTokenProviderFactory implements Factory<AuthTokenProvider> {
  private final Provider<AuthTokenManager> managerProvider;

  public ApiProvidesModule_ProvideAuthTokenProviderFactory(
      Provider<AuthTokenManager> managerProvider) {
    this.managerProvider = managerProvider;
  }

  @Override
  public AuthTokenProvider get() {
    return provideAuthTokenProvider(managerProvider.get());
  }

  public static ApiProvidesModule_ProvideAuthTokenProviderFactory create(
      Provider<AuthTokenManager> managerProvider) {
    return new ApiProvidesModule_ProvideAuthTokenProviderFactory(managerProvider);
  }

  public static AuthTokenProvider provideAuthTokenProvider(AuthTokenManager manager) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideAuthTokenProvider(manager));
  }
}
