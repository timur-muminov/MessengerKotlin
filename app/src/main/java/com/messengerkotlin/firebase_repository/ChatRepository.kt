package com.messengerkotlin.firebase_repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.MessageModel
import kotlinx.coroutines.flow.collect
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRepository {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getActualMessage(
        chatId: String,
        callback: (MessageModel?) -> Unit
    ) {
        rootReference.child("Chats").child(chatId).child("ActualChat")
            .onValueEventFlow().collect { if (it is EventResponse.Changed) callback(it.snapshot.getValue(MessageModel::class.java)) }
    }


    suspend fun getMessagesFromStorageChat(
        chatId: String
    ): ArrayList<MessageModel> {
        val dataSnapshot = getMessagesFromStorageChatByChatId(chatId)
        val messages: ArrayList<MessageModel> = ArrayList()
        messages.take(5)
        dataSnapshot.children.forEach { snapshot -> messages.add(snapshot.getValue(MessageModel::class.java)!!) }
        return messages
    }


    private suspend fun getMessagesFromStorageChatByChatId(chatId: String): DataSnapshot {
        when (val eventResponse =
            rootReference.child("Chats").child(chatId).child("StorageChat").onSingleEvent()) {
            is EventResponse.Changed -> return eventResponse.snapshot
            is EventResponse.Cancelled -> throw IllegalStateException()
        }
    }

    suspend fun findChatById(currentUserId: String, otherUserId: String): String {
        when (val eventResponse =
            rootReference.child("UserChatsRegister").child(currentUserId).child(otherUserId)
                .onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> return eventResponse.snapshot.getValue(ChatInfoModel::class.java)!!.chatId
        }
    }

    fun createChats(currentUserId: String, otherUserId: String) {
        val currentUserChatInfoModel = ChatInfoModel(currentUserId + otherUserId, otherUserId)
        val otherUserChatInfoModel = ChatInfoModel(currentUserId + otherUserId, currentUserId)
        rootReference.child("UserChatsRegister").child(currentUserId).child(otherUserId)
            .setValue(currentUserChatInfoModel)
        rootReference.child("UserChatsRegister").child(otherUserId).child(currentUserId)
            .setValue(otherUserChatInfoModel)
    }
}