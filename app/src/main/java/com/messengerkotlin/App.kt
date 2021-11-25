package com.messengerkotlin

import android.app.Application
import com.messengerkotlin.firebase_repository.*

class App : Application() {

    val authenticationManager by lazy {  AuthenticationManager() }
    val userStatusRepository by lazy { UserStatusRepository() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object    {
        lateinit var instance: App
        private set
    }
}