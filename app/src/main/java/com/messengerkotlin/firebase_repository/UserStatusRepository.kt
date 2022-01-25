package com.messengerkotlin.firebase_repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.core.firebase_hierarchy.FBNames
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class UserStatusRepository(private val ioDispatcher: CoroutineDispatcher, fbNames: FBNames) {

    private val statusReference =
        FirebaseDatabase.getInstance().reference.child(fbNames.userConnectionStatus)

    suspend fun setStatus(currentUserId: String, status: Status)  = withContext(ioDispatcher){
        statusReference.child(currentUserId).setValue(status)
    }

    suspend fun getStatus(otherUserId: String) : Flow<Status> = withContext(ioDispatcher) {
       statusReference.child(otherUserId).onValueEventFlow()
           .filter { ( it is EventResponse.Changed) }
           .mapNotNull { (it as EventResponse.Changed).snapshot.getValue(Status::class.java) }
    }
}