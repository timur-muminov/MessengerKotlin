package com.messengerkotlin.di

import com.messengerkotlin.core.firebase_hierarchy.FBNames
import com.messengerkotlin.firebase_repository.*
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val repositoriesModule = module{
    single<AuthenticationManager> { AuthenticationManager(ioDispatcher = Dispatchers.IO, fbNames = get()) }
    single<ChatRepository> { ChatRepository(ioDispatcher = Dispatchers.IO, fbNames = get()) }
    single<CurrentUserRepository> { CurrentUserRepository(ioDispatcher = Dispatchers.IO, fbNames = get()) }
    single<OtherUserRepository> { OtherUserRepository(ioDispatcher = Dispatchers.IO, fbNames = get()) }
    single<UserStatusRepository> { UserStatusRepository(ioDispatcher = Dispatchers.IO, fbNames = get()) }

}

val helperModule = module {
    single <FBNames> { FBNames() }
}