package com.application.echo.core.api.media;

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
public final class MediaRepositoryImpl_Factory implements Factory<MediaRepositoryImpl> {
  private final Provider<MediaApiService> apiProvider;

  public MediaRepositoryImpl_Factory(Provider<MediaApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public MediaRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static MediaRepositoryImpl_Factory create(Provider<MediaApiService> apiProvider) {
    return new MediaRepositoryImpl_Factory(apiProvider);
  }

  public static MediaRepositoryImpl newInstance(MediaApiService api) {
    return new MediaRepositoryImpl(api);
  }
}
