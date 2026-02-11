package com.application.echo.core.api.di;

import com.application.echo.core.api.media.MediaApiService;
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
public final class ApiProvidesModule_ProvideMediaApiServiceFactory implements Factory<MediaApiService> {
  private final Provider<EchoHttpClient> clientProvider;

  public ApiProvidesModule_ProvideMediaApiServiceFactory(Provider<EchoHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public MediaApiService get() {
    return provideMediaApiService(clientProvider.get());
  }

  public static ApiProvidesModule_ProvideMediaApiServiceFactory create(
      Provider<EchoHttpClient> clientProvider) {
    return new ApiProvidesModule_ProvideMediaApiServiceFactory(clientProvider);
  }

  public static MediaApiService provideMediaApiService(EchoHttpClient client) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideMediaApiService(client));
  }
}
