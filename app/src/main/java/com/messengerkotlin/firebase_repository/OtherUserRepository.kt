package com.messengerkotlin.firebase_repository


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.flow.*
import kotlin.collections.forEach

class OtherUserRepository {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getOtherUsersInfoFromChatsRegister(
        currentUserId: String
    ): HashMap<String, UserModel> {
        val users: HashMap<String, UserModel> = HashMap()
        val dataSnapshot = getDataSnapshotById(currentUserId) // todo warning
        dataSnapshot
            .mapNotNull { snapshot -> snapshot.getValue(ChatInfoModel::class.java) }
            .collect { chatInfoModel ->
                getOtherUserById(chatInfoModel.otherUserId)?.let {
                    users[it.id] = it
                }
            }
        return users
    }

    private suspend fun getDataSnapshotById(currentUserId: String): Flow<DataSnapshot> =
        rootReference.child("UserChatsRegister").child(currentUserId).onValueEventFlow()
            .filter { it is EventResponse.Changed }.map { (it as EventResponse.Changed).snapshot }


    suspend fun getOtherUserById(userId: String): UserModel? {
        when (val eventResponse = rootReference.child("Users").child(userId).onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> return eventResponse.snapshot.getValue(UserModel::class.java)
        }
    }

    suspend fun getOtherUserIdByUserkey(
        currentUserId: String,
        userKey: String,
    ): String? {
        when (val response = rootReference.child("Users").onSingleEvent()) {
            is EventResponse.Cancelled -> throw java.lang.IllegalStateException()
            is EventResponse.Changed -> {
                response.snapshot.children
                    .mapNotNull { child -> child.getValue(UserModel::class.java) }
                    .filter { userModel -> userModel.id != currentUserId && userModel.userkey == userKey }
                    .map { userModel ->
                        return userModel.id
                    }
                return null
            }
        }
    }

}