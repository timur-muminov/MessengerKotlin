package com.messengerkotlin.firebase_repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.messengerkotlin.core.enums.CommonStatus

class AuthenticationManager {

    var currentUserId: String? = null

    init {
        if(FirebaseAuth.getInstance().currentUser != null){
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        }
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUserId = it.currentUser?.uid
        }
    }

    fun signUp(username: String, email: String, password: String, callback: (CommonStatus) -> Unit) {
        Log.e("lol","entered sign up")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("lol","entered in")
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                        Log.e("lol","entered in in ")

                        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString())
                        ref.child("id").setValue(currentUserId)
                        ref.child("username").setValue(username)
                        ref.child("token").setValue(tokenTask.result)

                        callback(CommonStatus.SUCCESS)
                    }
                }
            }
    }

    fun signIn(email: String, password: String, callback: (CommonStatus) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    callback(CommonStatus.SUCCESS)
                }
            }
    }
}