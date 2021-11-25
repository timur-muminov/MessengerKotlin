package com.messengerkotlin.firebase_repository


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.UserModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OtherUserRepository {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getOtherUsersInfoFromChatsRegister(
        currentUserId: String
    ): HashMap<String, UserModel> {
        val users: HashMap<String, UserModel> = HashMap()
        val dataSnapshot = getDataSnapshotById(currentUserId)
        dataSnapshot.children.mapNotNull { snapshot -> snapshot.getValue(ChatInfoModel::class.java) }
            .forEach { userModel ->
                getOtherUserById(userModel.otherUserId)?.let {
                    users[it.id] = it
                }
            }
        return users
    }

    private suspend fun getDataSnapshotById(currentUserId: String): DataSnapshot =
        suspendCoroutine { continuation ->
            rootReference.child("UserChatsRegister").child(currentUserId)
                .onValueEvent(onDataChanged = { continuation.resume(it) })
        }


    suspend fun getOtherUserById(userId: String): UserModel? =
        suspendCoroutine { continuation ->
            rootReference.child("Users").child(userId)
                .onSingleEvent(onDataChanged = { dataSnapshot ->
                    continuation.resume(dataSnapshot.getValue(UserModel::class.java))
                })
        }

    fun getOtherUserIdByUserkey(
        currentUserId: String,
        userKey: String,
        callback: (String?) -> Unit
    ) {
        var i = 0
        rootReference.child("Users").onSingleEvent(onDataChanged = { dataSnapshot ->
            for (snapshot in dataSnapshot.children) {
                val userModel = snapshot.getValue(UserModel::class.java)
                userModel?.userkey?.let {
                    if (userModel.id != currentUserId)
                        if (it == userKey) {
                            i++
                            callback(userModel.id)
                        }
                }


            }
            if (i == 0) callback(null)
        })
    }
}