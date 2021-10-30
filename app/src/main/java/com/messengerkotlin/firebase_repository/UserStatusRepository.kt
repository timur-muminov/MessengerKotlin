package com.messengerkotlin.firebase_repository

import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.extensions.onValueEvent

class UserStatusRepository(private val currentUserId: String) {

    private val rootReference = FirebaseDatabase.getInstance().reference

    private val statusReference = rootReference.child("UserConnectionStatus")

    fun setStatus(status: Status){
        statusReference.child(currentUserId).setValue(status)
    }

    fun getStatus(otherUserId: String, otherUserStatus: (Status) -> Unit){
        statusReference.child(otherUserId).onValueEvent(onDataChanged = { dataSnapshot ->
            otherUserStatus(dataSnapshot.getValue(Status::class.java) as Status)
        })
    }
}