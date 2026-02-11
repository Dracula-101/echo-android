package com.application.echo.core.websocket.di;

import com.application.echo.core.websocket.config.ReconnectionConfig;
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
public final class WebSocketProvidesModule_ProvideReconnectionConfigFactory implements Factory<ReconnectionConfig> {
  @Override
  public ReconnectionConfig get() {
    return provideReconnectionConfig();
  }

  public static WebSocketProvidesModule_ProvideReconnectionConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ReconnectionConfig provideReconnectionConfig() {
    return Preconditions.checkNotNullFromProvides(WebSocketProvidesModule.INSTANCE.provideReconnectionConfig());
  }

  private static final class InstanceHolder {
    static final WebSocketProvidesModule_ProvideReconnectionConfigFactory INSTANCE = new WebSocketProvidesModule_ProvideReconnectionConfigFactory();
  }
}
