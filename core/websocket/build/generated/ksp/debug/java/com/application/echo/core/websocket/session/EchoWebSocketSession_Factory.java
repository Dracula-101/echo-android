package com.application.echo.core.websocket.session;

import com.application.echo.core.websocket.config.WebSocketConfig;
import com.application.echo.core.websocket.connection.WebSocketConnection;
import com.application.echo.core.websocket.heartbeat.HeartbeatManager;
import com.application.echo.core.websocket.interceptor.MessageInterceptor;
import com.application.echo.core.websocket.logging.WebSocketLogger;
import com.application.echo.core.websocket.message.MessageSerializer;
import com.application.echo.core.websocket.reconnect.ReconnectionHandler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
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
public final class EchoWebSocketSession_Factory implements Factory<EchoWebSocketSession> {
  private final Provider<WebSocketConfig> configProvider;

  private final Provider<WebSocketConnection> connectionProvider;

  private final Provider<ReconnectionHandler> reconnectionHandlerProvider;

  private final Provider<HeartbeatManager> heartbeatManagerProvider;

  private final Provider<MessageSerializer> serializerProvider;

  private final Provider<Set<MessageInterceptor>> interceptorsProvider;

  private final Provider<WebSocketLogger> loggerProvider;

  public EchoWebSocketSession_Factory(Provider<WebSocketConfig> configProvider,
      Provider<WebSocketConnection> connectionProvider,
      Provider<ReconnectionHandler> reconnectionHandlerProvider,
      Provider<HeartbeatManager> heartbeatManagerProvider,
      Provider<MessageSerializer> serializerProvider,
      Provider<Set<MessageInterceptor>> interceptorsProvider,
      Provider<WebSocketLogger> loggerProvider) {
    this.configProvider = configProvider;
    this.connectionProvider = connectionProvider;
    this.reconnectionHandlerProvider = reconnectionHandlerProvider;
    this.heartbeatManagerProvider = heartbeatManagerProvider;
    this.serializerProvider = serializerProvider;
    this.interceptorsProvider = interceptorsProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public EchoWebSocketSession get() {
    return newInstance(configProvider.get(), connectionProvider.get(), reconnectionHandlerProvider.get(), heartbeatManagerProvider.get(), serializerProvider.get(), interceptorsProvider.get(), loggerProvider.get());
  }

  public static EchoWebSocketSession_Factory create(Provider<WebSocketConfig> configProvider,
      Provider<WebSocketConnection> connectionProvider,
      Provider<ReconnectionHandler> reconnectionHandlerProvider,
      Provider<HeartbeatManager> heartbeatManagerProvider,
      Provider<MessageSerializer> serializerProvider,
      Provider<Set<MessageInterceptor>> interceptorsProvider,
      Provider<WebSocketLogger> loggerProvider) {
    return new EchoWebSocketSession_Factory(configProvider, connectionProvider, reconnectionHandlerProvider, heartbeatManagerProvider, serializerProvider, interceptorsProvider, loggerProvider);
  }

  public static EchoWebSocketSession newInstance(WebSocketConfig config,
      WebSocketConnection connection, ReconnectionHandler reconnectionHandler,
      HeartbeatManager heartbeatManager, MessageSerializer serializer,
      Set<MessageInterceptor> interceptors, WebSocketLogger logger) {
    return new EchoWebSocketSession(config, connection, reconnectionHandler, heartbeatManager, serializer, interceptors, logger);
  }
}
