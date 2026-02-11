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
public final class MessageRepositoryImpl_Factory implements Factory<MessageRepositoryImpl> {
  private final Provider<MessageApiService> apiProvider;

  public MessageRepositoryImpl_Factory(Provider<MessageApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public MessageRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static MessageRepositoryImpl_Factory create(Provider<MessageApiService> apiProvider) {
    return new MessageRepositoryImpl_Factory(apiProvider);
  }

  public static MessageRepositoryImpl newInstance(MessageApiService api) {
    return new MessageRepositoryImpl(api);
  }
}
