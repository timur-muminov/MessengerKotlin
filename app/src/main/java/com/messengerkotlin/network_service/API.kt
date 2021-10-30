package com.messengerkotlin.network_service

import com.messengerkotlin.models.Sender
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface API {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAtzGkxbw:APA91bGQyJPPrjFAHSVy1sbamawd73Co5xt2ogRj8WjR_-BqK9maOjaSh2FNc3c_XMHwx64DwooWz2HT7r6nuWlDbysOBRrDoFovXKjI_nW1vto5gaMA2P-C2WJLuoSZSs4amye8Mt4r"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<ResponseBody>
}