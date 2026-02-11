package com.application.echo.core.websocket.interceptor;

import com.application.echo.core.websocket.config.WebSocketConfig;
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
public final class LoggingInterceptor_Factory implements Factory<LoggingInterceptor> {
  private final Provider<WebSocketConfig> configProvider;

  public LoggingInterceptor_Factory(Provider<WebSocketConfig> configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public LoggingInterceptor get() {
    return newInstance(configProvider.get());
  }

  public static LoggingInterceptor_Factory create(Provider<WebSocketConfig> configProvider) {
    return new LoggingInterceptor_Factory(configProvider);
  }

  public static LoggingInterceptor newInstance(WebSocketConfig config) {
    return new LoggingInterceptor(config);
  }
}
