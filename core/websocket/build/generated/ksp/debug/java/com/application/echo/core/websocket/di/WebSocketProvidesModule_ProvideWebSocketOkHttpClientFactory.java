package com.application.echo.core.websocket.di;

import com.application.echo.core.websocket.config.WebSocketConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.application.echo.core.websocket.qualifier.WebSocketOkHttp")
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
public final class WebSocketProvidesModule_ProvideWebSocketOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<WebSocketConfig> configProvider;

  public WebSocketProvidesModule_ProvideWebSocketOkHttpClientFactory(
      Provider<WebSocketConfig> configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideWebSocketOkHttpClient(configProvider.get());
  }

  public static WebSocketProvidesModule_ProvideWebSocketOkHttpClientFactory create(
      Provider<WebSocketConfig> configProvider) {
    return new WebSocketProvidesModule_ProvideWebSocketOkHttpClientFactory(configProvider);
  }

  public static OkHttpClient provideWebSocketOkHttpClient(WebSocketConfig config) {
    return Preconditions.checkNotNullFromProvides(WebSocketProvidesModule.INSTANCE.provideWebSocketOkHttpClient(config));
  }
}
