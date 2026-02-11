package com.application.echo.core.api.di;

import com.application.echo.core.api.auth.AuthApiService;
import com.application.echo.core.network.client.EchoHttpClient;
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
public final class ApiProvidesModule_ProvideAuthApiServiceFactory implements Factory<AuthApiService> {
  private final Provider<EchoHttpClient> clientProvider;

  public ApiProvidesModule_ProvideAuthApiServiceFactory(Provider<EchoHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public AuthApiService get() {
    return provideAuthApiService(clientProvider.get());
  }

  public static ApiProvidesModule_ProvideAuthApiServiceFactory create(
      Provider<EchoHttpClient> clientProvider) {
    return new ApiProvidesModule_ProvideAuthApiServiceFactory(clientProvider);
  }

  public static AuthApiService provideAuthApiService(EchoHttpClient client) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideAuthApiService(client));
  }
}
