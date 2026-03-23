package com.application.echo.core.api.session;

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
public final class SessionHeaderInterceptor_Factory implements Factory<SessionHeaderInterceptor> {
  private final Provider<SessionProvider> sessionProvider;

  public SessionHeaderInterceptor_Factory(Provider<SessionProvider> sessionProvider) {
    this.sessionProvider = sessionProvider;
  }

  @Override
  public SessionHeaderInterceptor get() {
    return newInstance(sessionProvider.get());
  }

  public static SessionHeaderInterceptor_Factory create(Provider<SessionProvider> sessionProvider) {
    return new SessionHeaderInterceptor_Factory(sessionProvider);
  }

  public static SessionHeaderInterceptor newInstance(SessionProvider sessionProvider) {
    return new SessionHeaderInterceptor(sessionProvider);
  }
}
