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
public final class MediaApiRepositoryImpl_Factory implements Factory<MediaApiRepositoryImpl> {
  private final Provider<MediaApiService> apiProvider;

  public MediaApiRepositoryImpl_Factory(Provider<MediaApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public MediaApiRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static MediaApiRepositoryImpl_Factory create(Provider<MediaApiService> apiProvider) {
    return new MediaApiRepositoryImpl_Factory(apiProvider);
  }

  public static MediaApiRepositoryImpl newInstance(MediaApiService api) {
    return new MediaApiRepositoryImpl(api);
  }
}
