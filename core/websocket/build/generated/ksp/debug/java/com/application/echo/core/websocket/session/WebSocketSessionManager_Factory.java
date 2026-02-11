package com.application.echo.core.websocket.session;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WebSocketSessionManager_Factory implements Factory<WebSocketSessionManager> {
  private final Provider<WebSocketSession> sessionProvider;

  public WebSocketSessionManager_Factory(Provider<WebSocketSession> sessionProvider) {
    this.sessionProvider = sessionProvider;
  }

  @Override
  public WebSocketSessionManager get() {
    return newInstance(sessionProvider);
  }

  public static WebSocketSessionManager_Factory create(Provider<WebSocketSession> sessionProvider) {
    return new WebSocketSessionManager_Factory(sessionProvider);
  }

  public static WebSocketSessionManager newInstance(
      javax.inject.Provider<WebSocketSession> sessionProvider) {
    return new WebSocketSessionManager(sessionProvider);
  }
}
