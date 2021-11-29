package com.messengerkotlin.firebase_repository.messaging

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.models.UserModel

class MessagingManager(userStatusRepository: UserStatusRepository, chatId: String, otherUserId: String, currentUserModel: UserModel) {

    private val chatReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Chats").child(chatId)

    private var actualChatReference = chatReference.child("LastMessage")
    private var storageChatReference = chatReference.child("MessageStorage")
    private lateinit var otherUserStatus: Status
    private val messageMap: HashMap<String, String> = HashMap()
    private val notificationManager: NotificationManager = NotificationManager(currentUserModel, otherUserId)

    init {
        messageMap["senderId"] = currentUserModel.id
        userStatusRepository.getStatus(currentUserModel.id){ otherUserStatus = it}
    }

    fun sendMessage(message: String){
        messageMap["message"] = message
        actualChatReference.setValue(messageMap)
        storageChatReference.push().setValue(messageMap)

        if(otherUserStatus == Status.OFFLINE){
            notificationManager.sendNotification(message)
        }
    }
}