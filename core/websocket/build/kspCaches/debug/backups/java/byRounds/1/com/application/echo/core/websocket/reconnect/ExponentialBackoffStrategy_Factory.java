package com.application.echo.core.websocket.reconnect;

import com.application.echo.core.websocket.config.ReconnectionConfig;
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
public final class ExponentialBackoffStrategy_Factory implements Factory<ExponentialBackoffStrategy> {
  private final Provider<ReconnectionConfig> configProvider;

  public ExponentialBackoffStrategy_Factory(Provider<ReconnectionConfig> configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public ExponentialBackoffStrategy get() {
    return newInstance(configProvider.get());
  }

  public static ExponentialBackoffStrategy_Factory create(
      Provider<ReconnectionConfig> configProvider) {
    return new ExponentialBackoffStrategy_Factory(configProvider);
  }

  public static ExponentialBackoffStrategy newInstance(ReconnectionConfig config) {
    return new ExponentialBackoffStrategy(config);
  }
}
