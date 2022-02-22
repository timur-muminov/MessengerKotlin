package com.messengerkotlin.di

import com.messengerkotlin.firebase_repository.messaging.MessagingManager
import com.messengerkotlin.firebase_repository.messaging.NotificationManager
import com.messengerkotlin.network_service.API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class MessagingModule {

    @Provides
    fun provideMessagingManager(notificationManager: NotificationManager) : MessagingManager =
        MessagingManager(notificationManager = notificationManager)

    @Provides
    fun provideNotificationManager(api: API) : NotificationManager = NotificationManager(api = api)
}
