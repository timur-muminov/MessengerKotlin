package com.messengerkotlin.di

import com.messengerkotlin.firebase_repository.messaging.MessagingManager
import com.messengerkotlin.firebase_repository.messaging.NotificationManager
import org.koin.dsl.module

val messagingModule = module {
    factory<MessagingManager>{MessagingManager(fbNames = get(), notificationManager = get()) }
    factory<NotificationManager> { NotificationManager(fbNames = get(), api = get()) }
}
