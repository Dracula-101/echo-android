package com.application.echo.core.websocket.heartbeat;

import com.application.echo.core.websocket.config.HeartbeatConfig;
import com.application.echo.core.websocket.logging.WebSocketLogger;
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
public final class TickerHeartbeatManager_Factory implements Factory<TickerHeartbeatManager> {
  private final Provider<HeartbeatConfig> configProvider;

  private final Provider<WebSocketLogger> loggerProvider;

  public TickerHeartbeatManager_Factory(Provider<HeartbeatConfig> configProvider,
      Provider<WebSocketLogger> loggerProvider) {
    this.configProvider = configProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public TickerHeartbeatManager get() {
    return newInstance(configProvider.get(), loggerProvider.get());
  }

  public static TickerHeartbeatManager_Factory create(Provider<HeartbeatConfig> configProvider,
      Provider<WebSocketLogger> loggerProvider) {
    return new TickerHeartbeatManager_Factory(configProvider, loggerProvider);
  }

  public static TickerHeartbeatManager newInstance(HeartbeatConfig config, WebSocketLogger logger) {
    return new TickerHeartbeatManager(config, logger);
  }
}
