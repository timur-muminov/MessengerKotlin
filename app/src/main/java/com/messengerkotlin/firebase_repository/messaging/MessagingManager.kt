package com.messengerkotlin.firebase_repository.messaging

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.core.firebase_hierarchy.FBNames
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagingManager(private val fbNames: FBNames, private val notificationManager: NotificationManager) {

    private var chatReference: DatabaseReference? = null
    private var actualChatReference: DatabaseReference? = null
    private var storageChatReference: DatabaseReference? = null
    var otherUserStatus: Status = Status.OFFLINE
    private val messageMap: HashMap<String, String> = HashMap()

    fun create(currentUserId: String,chatId: String, otherUserId: String, currentUserModel: UserModel){
        messageMap["senderId"] = currentUserId
        chatReference = FirebaseDatabase.getInstance().reference.child(fbNames.chats).child(chatId)
        actualChatReference = chatReference?.child(fbNames.lastMessages)
        storageChatReference = chatReference?.child(fbNames.storageMessages)

        notificationManager.create(otherUserId)
        notificationManager.currentUserModel = currentUserModel
    }

    suspend fun sendMessage(message: String) = withContext(Dispatchers.IO){
        messageMap["message"] = message
        actualChatReference?.setValue(messageMap)
        storageChatReference?.push()?.setValue(messageMap)

        if(otherUserStatus == Status.OFFLINE){
            notificationManager.sendNotification(message)
        }
    }
}