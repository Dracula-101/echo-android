package com.application.echo.core.websocket.di;

import com.application.echo.core.websocket.config.WebSocketConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class WebSocketProvidesModule_ProvideWebSocketConfigFactory implements Factory<WebSocketConfig> {
  @Override
  public WebSocketConfig get() {
    return provideWebSocketConfig();
  }

  public static WebSocketProvidesModule_ProvideWebSocketConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WebSocketConfig provideWebSocketConfig() {
    return Preconditions.checkNotNullFromProvides(WebSocketProvidesModule.INSTANCE.provideWebSocketConfig());
  }

  private static final class InstanceHolder {
    static final WebSocketProvidesModule_ProvideWebSocketConfigFactory INSTANCE = new WebSocketProvidesModule_ProvideWebSocketConfigFactory();
  }
}
