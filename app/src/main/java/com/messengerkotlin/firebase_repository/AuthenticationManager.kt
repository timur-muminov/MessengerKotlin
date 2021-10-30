package com.messengerkotlin.firebase_repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.messengerkotlin.core.LoadingState
import com.messengerkotlin.core.enums.CommonStatus

class AuthenticationManager {

    private val currentUserIdLoadingStateMutableLiveData = MutableLiveData<LoadingState<String?, Nothing>>(LoadingState.Loading())

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUserIdLoadingStateMutableLiveData.postValue(LoadingState.Loaded(it.currentUser?.uid))
        }
    }

    fun signUp(
        username: String,
        email: String,
        password: String,
        callbackStatus: (CommonStatus) -> Unit
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.e("task", " done")
                if (task.isSuccessful) {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                        val userInfo: HashMap<String, String?> = HashMap()
                        userInfo["id"] = currentUserId
                        userInfo["username"] = username
                        userInfo["token"] = tokenTask.result

                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(currentUserId.toString()).setValue(userInfo)
                        callbackStatus(CommonStatus.SUCCESS)
                    }
                }
            }
    }

    fun signIn(email: String, password: String, callbackStatus: (CommonStatus) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callbackStatus(CommonStatus.SUCCESS)
                }
            }
    }

    fun getCurrentUserIdLoadingState() : LiveData<LoadingState<String?, Nothing>>{
        return currentUserIdLoadingStateMutableLiveData
    }


}