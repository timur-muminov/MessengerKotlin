package com.messengerkotlin.di

import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val repositoriesModule = module{
    single<AuthenticationManager> { AuthenticationManager(ioDispatcher = Dispatchers.IO) }
    single<ChatRepository> { ChatRepository(ioDispatcher = Dispatchers.IO) }
    single<CurrentUserRepository> { CurrentUserRepository(ioDispatcher = Dispatchers.IO) }
    single<OtherUserRepository> { OtherUserRepository(ioDispatcher = Dispatchers.IO) }
    single<UserStatusRepository> { UserStatusRepository(ioDispatcher = Dispatchers.IO) }

}

