package com.application.echo.core.websocket.connection;

import com.application.echo.core.websocket.logging.WebSocketLogger;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata
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
public final class OkHttpWebSocketConnection_Factory implements Factory<OkHttpWebSocketConnection> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<WebSocketFactory> webSocketFactoryProvider;

  private final Provider<WebSocketLogger> loggerProvider;

  public OkHttpWebSocketConnection_Factory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<WebSocketFactory> webSocketFactoryProvider,
      Provider<WebSocketLogger> loggerProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.webSocketFactoryProvider = webSocketFactoryProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public OkHttpWebSocketConnection get() {
    return newInstance(okHttpClientProvider.get(), webSocketFactoryProvider.get(), loggerProvider.get());
  }

  public static OkHttpWebSocketConnection_Factory create(
      Provider<OkHttpClient> okHttpClientProvider,
      Provider<WebSocketFactory> webSocketFactoryProvider,
      Provider<WebSocketLogger> loggerProvider) {
    return new OkHttpWebSocketConnection_Factory(okHttpClientProvider, webSocketFactoryProvider, loggerProvider);
  }

  public static OkHttpWebSocketConnection newInstance(OkHttpClient okHttpClient,
      WebSocketFactory webSocketFactory, WebSocketLogger logger) {
    return new OkHttpWebSocketConnection(okHttpClient, webSocketFactory, logger);
  }
}
