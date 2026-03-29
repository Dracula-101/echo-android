package com.application.echo.core.common.repository

import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> bufferedMutableSharedFlow(
    replay: Int = 0,
): MutableSharedFlow<T> =
    MutableSharedFlow(
        replay = replay,
        extraBufferCapacity = Int.MAX_VALUE,
    )