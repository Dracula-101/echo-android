package com.application.echo.core.navigation.di

import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.NavigatorImpl
import com.application.echo.core.navigation.interceptor.NavigationInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NavigationBindsModule {

    @Binds
    abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}

@Module
@InstallIn(SingletonComponent::class)
internal object NavigationProvidesModule {

    /**
     * Provides an empty default set of [NavigationInterceptor]s.
     *
     * Feature modules can contribute their own interceptors via `@IntoSet`:
     * ```
     * @Provides @IntoSet
     * fun provideAuthGuard(impl: AuthGuardInterceptor): NavigationInterceptor = impl
     * ```
     */
    @Provides
    @ElementsIntoSet
    fun provideDefaultInterceptors(): Set<NavigationInterceptor> = emptySet()
}
