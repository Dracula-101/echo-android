package com.application.echo.core.api.message;

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
public final class MessageApiRepositoryImpl_Factory implements Factory<MessageApiRepositoryImpl> {
  private final Provider<MessageApiService> apiProvider;

  public MessageApiRepositoryImpl_Factory(Provider<MessageApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public MessageApiRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static MessageApiRepositoryImpl_Factory create(Provider<MessageApiService> apiProvider) {
    return new MessageApiRepositoryImpl_Factory(apiProvider);
  }

  public static MessageApiRepositoryImpl newInstance(MessageApiService api) {
    return new MessageApiRepositoryImpl(api);
  }
}
