package com.messengerkotlin.firebase_repository

import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.extensions.onValueEvent

class UserStatusRepository() {

    private val statusReference = FirebaseDatabase.getInstance().reference.child("UserConnectionStatus")

    fun setStatus(currentUserId: String, status: Status){
        statusReference.child(currentUserId).setValue(status)
    }

    fun getStatus(otherUserId: String, callback: (Status) -> Unit){
        statusReference.child(otherUserId).onValueEvent(onDataChanged = { dataSnapshot ->
            callback(dataSnapshot.getValue(Status::class.java) as Status)
        })
    }
}