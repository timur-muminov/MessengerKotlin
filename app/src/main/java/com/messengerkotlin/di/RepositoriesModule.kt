package com.messengerkotlin.di

import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideAuthenticationManager():AuthenticationManager = AuthenticationManager(ioDispatcher = Dispatchers.IO)

    @Provides
    @Singleton
    fun provideChatRepository():ChatRepository = ChatRepository(ioDispatcher = Dispatchers.IO)

    @Provides
    @Singleton
    fun provideCurrentUserRepository():CurrentUserRepository = CurrentUserRepository(ioDispatcher = Dispatchers.IO)

    @Provides
    @Singleton
    fun provideOtherUserRepository():OtherUserRepository = OtherUserRepository(ioDispatcher = Dispatchers.IO)

    @Provides
    @Singleton
    fun provideUserStatusRepository(): UserStatusRepository = UserStatusRepository(ioDispatcher = Dispatchers.IO)

}