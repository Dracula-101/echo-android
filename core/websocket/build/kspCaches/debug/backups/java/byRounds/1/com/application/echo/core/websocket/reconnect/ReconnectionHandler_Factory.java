package com.application.echo.core.websocket.reconnect;

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
public final class ReconnectionHandler_Factory implements Factory<ReconnectionHandler> {
  private final Provider<ReconnectionStrategy> strategyProvider;

  private final Provider<WebSocketLogger> loggerProvider;

  public ReconnectionHandler_Factory(Provider<ReconnectionStrategy> strategyProvider,
      Provider<WebSocketLogger> loggerProvider) {
    this.strategyProvider = strategyProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public ReconnectionHandler get() {
    return newInstance(strategyProvider.get(), loggerProvider.get());
  }

  public static ReconnectionHandler_Factory create(Provider<ReconnectionStrategy> strategyProvider,
      Provider<WebSocketLogger> loggerProvider) {
    return new ReconnectionHandler_Factory(strategyProvider, loggerProvider);
  }

  public static ReconnectionHandler newInstance(ReconnectionStrategy strategy,
      WebSocketLogger logger) {
    return new ReconnectionHandler(strategy, logger);
  }
}
