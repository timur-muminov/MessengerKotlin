package com.messengerkotlin.firebase_repository

import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class UserStatusRepository() {

    private val statusReference =
        FirebaseDatabase.getInstance().reference.child("UserConnectionStatus")

    fun setStatus(currentUserId: String, status: Status) {
        statusReference.child(currentUserId).setValue(status)
    }

    suspend fun getStatus(otherUserId: String): Flow<Status> =
        statusReference.child(otherUserId).onValueEventFlow().filter { it is EventResponse.Changed }
            .map { (it as EventResponse.Changed).snapshot.getValue(Status::class.java)!! }
}