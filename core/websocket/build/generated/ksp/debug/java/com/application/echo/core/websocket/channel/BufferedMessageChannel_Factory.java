package com.application.echo.core.websocket.channel;

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
public final class BufferedMessageChannel_Factory implements Factory<BufferedMessageChannel> {
  @Override
  public BufferedMessageChannel get() {
    return newInstance();
  }

  public static BufferedMessageChannel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BufferedMessageChannel newInstance() {
    return new BufferedMessageChannel();
  }

  private static final class InstanceHolder {
    static final BufferedMessageChannel_Factory INSTANCE = new BufferedMessageChannel_Factory();
  }
}
