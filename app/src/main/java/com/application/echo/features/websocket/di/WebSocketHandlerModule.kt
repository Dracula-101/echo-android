package com.application.echo.features.websocket.di

import com.application.echo.core.websocket.handler.TypedMessageHandler
import com.application.echo.features.websocket.handler.MessageReadHandler
import com.application.echo.features.websocket.handler.PresenceChangedHandler
import com.application.echo.features.websocket.handler.SubscribedHandler
import com.application.echo.features.websocket.handler.SyncRequiredHandler
import com.application.echo.features.websocket.handler.TypingStartHandler
import com.application.echo.features.websocket.handler.TypingStopHandler
import com.application.echo.features.websocket.handler.NewMessageHandler
import com.application.echo.features.websocket.handler.WsErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class WebSocketHandlerModule {

    @Binds
    @IntoSet
    abstract fun bindSubscribedHandler(impl: SubscribedHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindTypingStartHandler(impl: TypingStartHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindTypingStopHandler(impl: TypingStopHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindPresenceChangedHandler(impl: PresenceChangedHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindMessageReadHandler(impl: MessageReadHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindSyncRequiredHandler(impl: SyncRequiredHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindNewMessageHandler(impl: NewMessageHandler): TypedMessageHandler

    @Binds
    @IntoSet
    abstract fun bindWsErrorHandler(impl: WsErrorHandler): TypedMessageHandler
}
