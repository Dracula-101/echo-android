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
public final class MessageBuffer_Factory implements Factory<MessageBuffer> {
  @Override
  public MessageBuffer get() {
    return newInstance();
  }

  public static MessageBuffer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MessageBuffer newInstance() {
    return new MessageBuffer();
  }

  private static final class InstanceHolder {
    static final MessageBuffer_Factory INSTANCE = new MessageBuffer_Factory();
  }
}
