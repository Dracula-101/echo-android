package com.application.echo.core.api.di;

import com.application.echo.core.api.session.SessionProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.application.echo.core.api.di.SessionInterceptor")
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
public final class ApiProvidesModule_ProvideSessionHeaderInterceptorFactory implements Factory<Interceptor> {
  private final Provider<SessionProvider> sessionProvider;

  public ApiProvidesModule_ProvideSessionHeaderInterceptorFactory(
      Provider<SessionProvider> sessionProvider) {
    this.sessionProvider = sessionProvider;
  }

  @Override
  public Interceptor get() {
    return provideSessionHeaderInterceptor(sessionProvider.get());
  }

  public static ApiProvidesModule_ProvideSessionHeaderInterceptorFactory create(
      Provider<SessionProvider> sessionProvider) {
    return new ApiProvidesModule_ProvideSessionHeaderInterceptorFactory(sessionProvider);
  }

  public static Interceptor provideSessionHeaderInterceptor(SessionProvider sessionProvider) {
    return Preconditions.checkNotNullFromProvides(ApiProvidesModule.INSTANCE.provideSessionHeaderInterceptor(sessionProvider));
  }
}
