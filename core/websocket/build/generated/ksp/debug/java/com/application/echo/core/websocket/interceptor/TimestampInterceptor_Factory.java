package com.application.echo.core.websocket.interceptor;

import com.google.gson.Gson;
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
public final class TimestampInterceptor_Factory implements Factory<TimestampInterceptor> {
  private final Provider<Gson> gsonProvider;

  public TimestampInterceptor_Factory(Provider<Gson> gsonProvider) {
    this.gsonProvider = gsonProvider;
  }

  @Override
  public TimestampInterceptor get() {
    return newInstance(gsonProvider.get());
  }

  public static TimestampInterceptor_Factory create(Provider<Gson> gsonProvider) {
    return new TimestampInterceptor_Factory(gsonProvider);
  }

  public static TimestampInterceptor newInstance(Gson gson) {
    return new TimestampInterceptor(gson);
  }
}
