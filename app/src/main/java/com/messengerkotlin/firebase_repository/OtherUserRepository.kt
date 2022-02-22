package com.messengerkotlin.firebase_repository


import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.utils.FBNames
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class OtherUserRepository(private val ioDispatcher: CoroutineDispatcher) {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getOtherUsersFromStorageChatsRegister(currentUserId: String): List<ChatInfoModel> =
        withContext(ioDispatcher) {
            when (val eventResponse = rootReference.child(FBNames.userChatsRegister).child(currentUserId)
                .child(FBNames.storageUsersChat).onSingleEvent()) {
                is EventResponse.Cancelled -> throw IllegalStateException()
                is EventResponse.Changed -> {
                    eventResponse.snapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(
                            ChatInfoModel::class.java
                        )
                    }
                }
            }
        }

    suspend fun getOtherUsersFromLastChatsRegister(currentUserId: String): Flow<ChatInfoModel> =
        withContext(ioDispatcher) {
            rootReference.child(FBNames.userChatsRegister).child(currentUserId).child(FBNames.lastUserChat).onValueEventFlow()
                .filter { it is EventResponse.Changed }
                .mapNotNull { (it as EventResponse.Changed).snapshot.getValue(ChatInfoModel::class.java) }
        }

    suspend fun getOtherUserById(userId: String): UserModel = withContext(ioDispatcher) {
        when (val eventResponse =
            rootReference.child(FBNames.users).child(userId).onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> eventResponse.snapshot.getValue(UserModel::class.java)!!
        }
    }

    suspend fun getOtherUserIdByUserkey(
        userKey: String,
    ): String? = withContext(ioDispatcher) {
         when (val response = rootReference.child(FBNames.userKeys).child(userKey).onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> {
                return@withContext if (response.snapshot.exists()){
                    response.snapshot.getValue(String::class.java)
                } else {
                    null
                }
            }
        }
    }
}