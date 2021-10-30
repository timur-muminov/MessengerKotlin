package com.messengerkotlin.firebase_repository.messaging

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.models.Data
import com.messengerkotlin.models.Sender
import com.messengerkotlin.models.UserModel
import com.messengerkotlin.network_service.API
import com.messengerkotlin.network_service.NetworkService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationManager(var currentUserModel: UserModel, var otherUserId: String) {

    private val commonReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val tokenReference: DatabaseReference = commonReference.child("Users").child(otherUserId).child("token")
    private val apiService: API = NetworkService.apiService

    fun sendNotification(message: String) {
        tokenReference.onSingleEvent( onDataChanged = { dataSnapshot ->
            val token: String = dataSnapshot.getValue(String::class.java).toString()
            val data = Data(otherUserId, currentUserModel.id, currentUserModel.username, currentUserModel.imageurl, message)
            val sender = Sender(data, token)
            apiService.sendNotification(sender).enqueue(object : Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        })
    }
}