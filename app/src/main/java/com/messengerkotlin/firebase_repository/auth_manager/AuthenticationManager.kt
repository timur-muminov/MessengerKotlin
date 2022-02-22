package com.messengerkotlin.firebase_repository.auth_manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.messengerkotlin.core.utils.FBNames
import com.messengerkotlin.firebase_repository.auth_manager.enums.AuthStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationManager @Inject constructor(private val ioDispatcher: CoroutineDispatcher) {

    var currentUserId: String? = null

    init {
        if (FirebaseAuth.getInstance().currentUser != null) {
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        }
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUserId = it.currentUser?.uid
        }
    }

    suspend fun signUp(username: String, email: String, password: String): AuthStatus =
        withContext(ioDispatcher) {
            suspendCoroutine { continuation ->
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                                currentUserId?.let {
                                    val map: HashMap<String, String?> = HashMap()
                                    map["id"] = currentUserId
                                    map["username"] = username
                                    map["token"] = tokenTask.result

                                    FirebaseDatabase.getInstance().reference.child(FBNames.users).child(currentUserId.toString()).setValue(map)

                                }
                                continuation.resume(AuthStatus.SUCCESS)
                            }
                        }
                    }
            }
        }

    suspend fun signIn(email: String, password: String): AuthStatus = withContext(ioDispatcher) {
        suspendCoroutine { continuation ->
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                        continuation.resume(AuthStatus.SUCCESS)
                    }
                }
        }
    }
}