package com.application.echo.core.websocket.di;

import com.application.echo.core.websocket.config.HeartbeatConfig;
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
public final class WebSocketProvidesModule_ProvideHeartbeatConfigFactory implements Factory<HeartbeatConfig> {
  @Override
  public HeartbeatConfig get() {
    return provideHeartbeatConfig();
  }

  public static WebSocketProvidesModule_ProvideHeartbeatConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HeartbeatConfig provideHeartbeatConfig() {
    return Preconditions.checkNotNullFromProvides(WebSocketProvidesModule.INSTANCE.provideHeartbeatConfig());
  }

  private static final class InstanceHolder {
    static final WebSocketProvidesModule_ProvideHeartbeatConfigFactory INSTANCE = new WebSocketProvidesModule_ProvideHeartbeatConfigFactory();
  }
}
