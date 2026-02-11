package com.application.echo.feature.auth.signup;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SignUpViewModel_Factory implements Factory<SignUpViewModel> {
  @Override
  public SignUpViewModel get() {
    return newInstance();
  }

  public static SignUpViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SignUpViewModel newInstance() {
    return new SignUpViewModel();
  }

  private static final class InstanceHolder {
    static final SignUpViewModel_Factory INSTANCE = new SignUpViewModel_Factory();
  }
}
