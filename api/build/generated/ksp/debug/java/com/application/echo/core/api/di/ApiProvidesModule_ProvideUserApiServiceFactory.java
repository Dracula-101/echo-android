package com.application.echo.core.api.di;

import com.application.echo.core.api.user.UserApiService;
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
public final class ApiProvidesModule_ProvideUserApiServiceFactory implements Factory<UserApiService> {
  private final Provider<EchoHttpClient> clientProvider;

  public ApiProvidesModule_ProvideUserApiServiceFactory(Provider<EchoHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public UserApiService get() {
    return provideUserApiService(clientProvider.get());
  }

  public static ApiProvidesModule_ProvideUserApiServiceFactory create(
      Provider<EchoHttpClient> clientProvider) {
    return new ApiProvidesModule_ProvideUserApiServiceFactory(clientProvider);
  }

  public static UserApiService provideUserApiService(EchoHttpClient client) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideUserApiService(client));
  }
}
