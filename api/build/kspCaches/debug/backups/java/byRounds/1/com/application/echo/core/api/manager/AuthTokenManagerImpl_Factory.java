package com.application.echo.core.api.manager;

import android.content.SharedPreferences;
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
public final class AuthTokenManagerImpl_Factory implements Factory<AuthTokenManagerImpl> {
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  public AuthTokenManagerImpl_Factory(Provider<SharedPreferences> sharedPreferencesProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public AuthTokenManagerImpl get() {
    return newInstance(sharedPreferencesProvider.get());
  }

  public static AuthTokenManagerImpl_Factory create(
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new AuthTokenManagerImpl_Factory(sharedPreferencesProvider);
  }

  public static AuthTokenManagerImpl newInstance(SharedPreferences sharedPreferences) {
    return new AuthTokenManagerImpl(sharedPreferences);
  }
}
