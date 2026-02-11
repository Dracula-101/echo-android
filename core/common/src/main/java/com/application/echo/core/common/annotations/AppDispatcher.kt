package com.application.echo.core.common.annotations

import com.application.echo.core.common.model.AppDispatchers
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class AppDispatcher(val appDispatcher: AppDispatchers)