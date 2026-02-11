package com.application.echo.core.websocket.logging;

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
public final class WebSocketLogger_Factory implements Factory<WebSocketLogger> {
  private final Provider<WebSocketConfig> configProvider;

  public WebSocketLogger_Factory(Provider<WebSocketConfig> configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public WebSocketLogger get() {
    return newInstance(configProvider.get());
  }

  public static WebSocketLogger_Factory create(Provider<WebSocketConfig> configProvider) {
    return new WebSocketLogger_Factory(configProvider);
  }

  public static WebSocketLogger newInstance(WebSocketConfig config) {
    return new WebSocketLogger(config);
  }
}
