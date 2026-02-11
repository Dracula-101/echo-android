package com.application.echo.core.websocket.interceptor;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class NoOpTokenProvider_Factory implements Factory<NoOpTokenProvider> {
  @Override
  public NoOpTokenProvider get() {
    return newInstance();
  }

  public static NoOpTokenProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NoOpTokenProvider newInstance() {
    return new NoOpTokenProvider();
  }

  private static final class InstanceHolder {
    static final NoOpTokenProvider_Factory INSTANCE = new NoOpTokenProvider_Factory();
  }
}
