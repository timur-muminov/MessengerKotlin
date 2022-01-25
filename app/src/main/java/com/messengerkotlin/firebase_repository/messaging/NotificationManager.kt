package com.messengerkotlin.firebase_repository.messaging

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.firebase_hierarchy.FBNames
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.models.*
import com.messengerkotlin.network_service.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationManager(private val fbNames: FBNames, private val api: API) {

    private val commonReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var tokenReference: DatabaseReference? = null

    var currentUserModel: UserModel? = null
    private var otherUserId: String = ""

    fun create(otherUserId: String) {
        this.otherUserId = otherUserId
        tokenReference = commonReference.child(fbNames.users).child(otherUserId).child("token")
    }

    suspend fun sendNotification(message: String) = withContext(Dispatchers.IO) {
        when (val response = tokenReference?.onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> {

                val token: String? = response.snapshot.getValue(String::class.java)
                Log.e("message"," token received " + token)
                currentUserModel?.let { userModel ->
                    token?.let { tok ->
                        val data = DataModel(
                            otherUserId,
                            userModel.id,
                            userModel.username,
                            userModel.imageurl?: "",
                            message
                        )
                        Log.e("message"," tok = " + data)
                        val sender = SenderModel(tok, data)
                        api.sendNotification(sender)
                    }
                }
            }
            else -> {}
        }
    }
}