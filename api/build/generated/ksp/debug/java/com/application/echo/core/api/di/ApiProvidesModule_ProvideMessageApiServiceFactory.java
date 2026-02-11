package com.application.echo.core.api.di;

import com.application.echo.core.api.message.MessageApiService;
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
public final class ApiProvidesModule_ProvideMessageApiServiceFactory implements Factory<MessageApiService> {
  private final Provider<EchoHttpClient> clientProvider;

  public ApiProvidesModule_ProvideMessageApiServiceFactory(
      Provider<EchoHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public MessageApiService get() {
    return provideMessageApiService(clientProvider.get());
  }

  public static ApiProvidesModule_ProvideMessageApiServiceFactory create(
      Provider<EchoHttpClient> clientProvider) {
    return new ApiProvidesModule_ProvideMessageApiServiceFactory(clientProvider);
  }

  public static MessageApiService provideMessageApiService(EchoHttpClient client) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideMessageApiService(client));
  }
}
