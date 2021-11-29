package com.messengerkotlin.firebase_repository.extensions

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.messengerkotlin.core.EventResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun DatabaseReference.onValueEventFlow(): Flow<EventResponse> = callbackFlow {

    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySend(EventResponse.Changed(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            trySend(EventResponse.Cancelled(error))
        }
    }
    addValueEventListener(valueEventListener)
    awaitClose { removeEventListener(valueEventListener) }
}

suspend fun DatabaseReference.onSingleEvent(): EventResponse =
    suspendCoroutine { continuation ->
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(EventResponse.Changed(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(EventResponse.Cancelled(error))
            }
        }
        addListenerForSingleValueEvent(valueEventListener)
    }

