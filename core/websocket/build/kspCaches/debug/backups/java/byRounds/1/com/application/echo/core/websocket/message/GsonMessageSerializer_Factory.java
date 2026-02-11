package com.application.echo.core.websocket.message;

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
public final class GsonMessageSerializer_Factory implements Factory<GsonMessageSerializer> {
  private final Provider<Gson> gsonProvider;

  public GsonMessageSerializer_Factory(Provider<Gson> gsonProvider) {
    this.gsonProvider = gsonProvider;
  }

  @Override
  public GsonMessageSerializer get() {
    return newInstance(gsonProvider.get());
  }

  public static GsonMessageSerializer_Factory create(Provider<Gson> gsonProvider) {
    return new GsonMessageSerializer_Factory(gsonProvider);
  }

  public static GsonMessageSerializer newInstance(Gson gson) {
    return new GsonMessageSerializer(gson);
  }
}
