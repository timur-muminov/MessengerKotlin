package com.messengerkotlin.firebase_repository

import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.MessageModel

class ChatRepository(private val currentUserId: String) {

    private val rootReference = FirebaseDatabase.getInstance().reference

    fun getLastMessage(otherUserId: String, lastMessage: (MessageModel) -> Unit) {
        getMessagesFromChat(otherUserId) { messages ->
            lastMessage(messages[messages.size - 1])
        }
    }

    fun getMessagesFromChat(
        otherUserId: String,
        transferMessages: (ArrayList<MessageModel>) -> Unit
    ) {
        findChatById(otherUserId) { chatId ->
            rootReference.child("Chats").child(chatId)
                .onValueEvent(onDataChanged = { dataSnapshot1 ->
                    val messages: ArrayList<MessageModel> = ArrayList()
                    for (snapshot in dataSnapshot1.children) {
                        val messageModel: MessageModel =
                            snapshot.getValue(MessageModel::class.java)!!
                        messages.add(messageModel)
                    }
                    transferMessages(messages)
                })
        }
    }

    fun findChatById(otherUserId: String, resultChatId: (String) -> Unit) {
        rootReference.child("UserChatsRegister").child(currentUserId).child(otherUserId)
            .onSingleEvent(onDataChanged = { dataSnapshot ->
                val chatInfoModel: ChatInfoModel =
                    dataSnapshot.getValue(ChatInfoModel::class.java)!!
                resultChatId(chatInfoModel.chatId)
            })
    }

    fun createChat(userId: String) {
        val chatInfoModel = ChatInfoModel(currentUserId + userId, userId)
        rootReference.child("UserChatsRegister").child(currentUserId).child(userId).setValue(chatInfoModel)
    }
}