package com.application.echo.core.websocket.connection;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WebSocketFactory_Factory implements Factory<WebSocketFactory> {
  @Override
  public WebSocketFactory get() {
    return newInstance();
  }

  public static WebSocketFactory_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WebSocketFactory newInstance() {
    return new WebSocketFactory();
  }

  private static final class InstanceHolder {
    static final WebSocketFactory_Factory INSTANCE = new WebSocketFactory_Factory();
  }
}
