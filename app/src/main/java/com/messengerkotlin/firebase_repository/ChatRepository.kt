package com.messengerkotlin.firebase_repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.utils.FBNames
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.MessageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class ChatRepository(private val ioDispatcher: CoroutineDispatcher) {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getLastMessage(
        chatId: String,
    ): Flow<MessageModel> = withContext(ioDispatcher) {
        rootReference.child(FBNames.chats).child(chatId).child(FBNames.lastMessages)
            .onValueEventFlow().filter {
                it is EventResponse.Changed }
            .mapNotNull {
                (it as EventResponse.Changed).snapshot.getValue(MessageModel::class.java)
            }
    }


    suspend fun getMessagesFromStorageChat(
        chatId: String
    ): ArrayList<MessageModel> = withContext(ioDispatcher) {
        val dataSnapshot = getMessagesFromStorageChatByChatId(chatId)
        val messages: ArrayList<MessageModel> = ArrayList()
        dataSnapshot.children.forEach { snapshot -> messages.add(snapshot.getValue(MessageModel::class.java)!!) }
        messages
    }


    private suspend fun getMessagesFromStorageChatByChatId(chatId: String): DataSnapshot =
        withContext(ioDispatcher) {
            when (val eventResponse =
                rootReference.child(FBNames.chats).child(chatId).child(FBNames.storageMessages).onSingleEvent()) {
                is EventResponse.Changed -> eventResponse.snapshot
                is EventResponse.Cancelled -> throw IllegalStateException()
            }
        }

    suspend fun findChatById(currentUserId: String, otherUserId: String): ChatInfoModel =
        withContext(ioDispatcher) {
            when (val eventResponse =
                rootReference.child(FBNames.userChatsRegister).child(currentUserId).child(FBNames.storageUsersChat).child(otherUserId)
                    .onSingleEvent()) {
                is EventResponse.Cancelled -> throw IllegalStateException()
                is EventResponse.Changed -> eventResponse.snapshot.getValue(ChatInfoModel::class.java)!!
            }
        }

    suspend fun createChats(currentUserId: String, otherUserId: String) =
        withContext(ioDispatcher) {
            val currentUserChatInfoModel = ChatInfoModel(currentUserId + otherUserId, otherUserId)
            val otherUserChatInfoModel = ChatInfoModel(currentUserId + otherUserId, currentUserId)

            val currentUserChats = rootReference.child(FBNames.userChatsRegister).child(currentUserId)
            val otherUserChats = rootReference.child(FBNames.userChatsRegister).child(otherUserId)

            currentUserChats.child(FBNames.storageUsersChat).child(otherUserId).setValue(currentUserChatInfoModel)
            currentUserChats.child(FBNames.lastUserChat).setValue(currentUserChatInfoModel)

            otherUserChats.child(FBNames.storageUsersChat).child(currentUserId).setValue(otherUserChatInfoModel)
            otherUserChats.child(FBNames.lastUserChat).setValue(otherUserChatInfoModel)
        }
}