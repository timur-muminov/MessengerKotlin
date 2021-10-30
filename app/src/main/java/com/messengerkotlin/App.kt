package com.messengerkotlin

import android.app.Application
import com.messengerkotlin.firebase_repository.*

class App : Application() {

    val authenticationManager by lazy {  AuthenticationManager() }

    val currentUserRepository by lazy { CurrentUserRepository(authenticationManager) }
    val otherUserRepository by lazy { OtherUserRepository(authenticationManager) }
    val userStatusRepository by lazy { UserStatusRepository(authenticationManager) }
    val chatRepository by lazy { ChatRepository(authenticationManager) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
    }
}