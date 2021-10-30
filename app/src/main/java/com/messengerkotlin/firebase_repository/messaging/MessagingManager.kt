package com.messengerkotlin.firebase_repository.messaging

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.models.UserModel

class MessagingManager(var chatId: String, otherUserId: String, currentUserModel: UserModel) {

    private val commonReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private var chatsReference = commonReference.child("Chats")
    private lateinit var otherUserStatus: Status
    private val messageMap: HashMap<String, String> = HashMap()
    private val notificationManager: NotificationManager = NotificationManager(currentUserModel, otherUserId)

    init {
        messageMap["sender"] = currentUserModel.id
        UserManager.firebaseRepository?.getStatus(currentUserModel.id){ otherUserStatus = it}
    }

    fun sendMessage(message: String){
        messageMap["message"] = message
        chatsReference.child(chatId).push().setValue(messageMap)

        if(otherUserStatus == Status.OFFLINE){
            notificationManager.sendNotification(message)
        }
    }
}