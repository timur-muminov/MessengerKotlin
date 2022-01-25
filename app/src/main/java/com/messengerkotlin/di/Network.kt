package com.messengerkotlin.di

import com.messengerkotlin.network_service.API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {

    single<API> {
        get<Retrofit>().create(API::class.java)
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }
}