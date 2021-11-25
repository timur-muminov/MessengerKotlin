package com.messengerkotlin.firebase_repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.ChatInfoModel

import com.messengerkotlin.models.MessageModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRepository {

    private val rootReference = FirebaseDatabase.getInstance().reference

    suspend fun getLastMessage(
        currentUserId: String,
        otherUserId: String
    ): MessageModel? {
        val messages = getMessagesFromChat(currentUserId, otherUserId)
        if (messages.size != 0)
            return messages[messages.size - 1]
        else return null
    }


    suspend fun getMessagesFromChat(
        currentUserId: String,
        otherUserId: String,
    ): ArrayList<MessageModel> {
        val chatId = findChatById(currentUserId, otherUserId)
        val dataSnapshot = getMessagesFromChatByChatId(chatId)
        val messages: ArrayList<MessageModel> = ArrayList()
        dataSnapshot.children.forEach { snapshot -> messages.add(snapshot.getValue(MessageModel::class.java)!!) }
        return messages
    }


    private suspend fun getMessagesFromChatByChatId(chatId: String): DataSnapshot =
        suspendCoroutine { continuation ->
            rootReference.child("Chats").child(chatId)
                .onValueEvent(onDataChanged = { continuation.resume(it) })
        }

    suspend fun findChatById(currentUserId: String, otherUserId: String): String =
        suspendCoroutine { continuation ->
            rootReference.child("UserChatsRegister").child(currentUserId).child(otherUserId)
                .onSingleEvent(onDataChanged = { dataSnapshot ->
                    val chatInfoModel: ChatInfoModel =
                        dataSnapshot.getValue(ChatInfoModel::class.java)!!
                    continuation.resume(chatInfoModel.chatId)
                })
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