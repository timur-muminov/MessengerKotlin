package com.messengerkotlin.firebase_repository.extensions

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

fun DatabaseReference.onValueEvent(onDataChanged: (DataSnapshot) -> Unit = {}, onCancelled: (DatabaseError) -> Unit = {}) {

    addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onDataChanged(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            onCancelled(error)
        }
    })
}

fun DatabaseReference.onSingleEvent(onDataChanged: (DataSnapshot) -> Unit = {}, onCancelled: () -> Unit = {}) {

    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onDataChanged(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            onCancelled()
        }
    })
}