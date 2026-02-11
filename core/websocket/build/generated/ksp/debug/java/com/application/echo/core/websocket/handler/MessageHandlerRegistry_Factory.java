package com.application.echo.core.websocket.handler;

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
public final class MessageHandlerRegistry_Factory implements Factory<MessageHandlerRegistry> {
  @Override
  public MessageHandlerRegistry get() {
    return newInstance();
  }

  public static MessageHandlerRegistry_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MessageHandlerRegistry newInstance() {
    return new MessageHandlerRegistry();
  }

  private static final class InstanceHolder {
    static final MessageHandlerRegistry_Factory INSTANCE = new MessageHandlerRegistry_Factory();
  }
}
