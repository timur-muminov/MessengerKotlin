package com.messengerkotlin.fragments.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.firebase_repository.messaging.MessagingManager
import com.messengerkotlin.models.MessageModel
import com.messengerkotlin.models.UserModel

class ChatroomViewModel(var userId: String): ViewModel() {

    private val _otherUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var otherUserLiveData: LiveData<UserModel> = _otherUserMutableLiveData

    private val _messagesMutableLiveData: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData()
    var messagesLiveData: LiveData<ArrayList<MessageModel>> = _messagesMutableLiveData

    private lateinit var messagingManager: MessagingManager

    init {
        UserManager.firebaseRepository?.getOtherUserInfo(userId){ userModel ->
            _otherUserMutableLiveData.postValue(userModel)
            UserManager.firebaseRepository?.findChatById(userId){ messagingManager = MessagingManager(it, userId ,userModel!!) }
        }
        UserManager.firebaseRepository?.getMessagesFromChat(userId){_messagesMutableLiveData.postValue(it)}
    }

    fun sendMessage(message: String) {
        messagingManager.sendMessage(message)
    }
}