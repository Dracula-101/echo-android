package com.application.echo.core.api.di;

import android.content.SharedPreferences;
import com.application.echo.core.api.manager.AuthTokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.application.echo.core.common.annotations.UnencryptedPreferences")
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
public final class ApiProvidesModule_ProvideAuthTokenManagerFactory implements Factory<AuthTokenManager> {
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  public ApiProvidesModule_ProvideAuthTokenManagerFactory(
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public AuthTokenManager get() {
    return provideAuthTokenManager(sharedPreferencesProvider.get());
  }

  public static ApiProvidesModule_ProvideAuthTokenManagerFactory create(
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new ApiProvidesModule_ProvideAuthTokenManagerFactory(sharedPreferencesProvider);
  }

  public static AuthTokenManager provideAuthTokenManager(SharedPreferences sharedPreferences) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideAuthTokenManager(sharedPreferences));
  }
}
