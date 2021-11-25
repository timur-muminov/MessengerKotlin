package com.messengerkotlin.fragments.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.*
import com.messengerkotlin.firebase_repository.messaging.MessagingManager
import com.messengerkotlin.models.MessageModel
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.launch

class ChatroomViewModel(
    authenticationManager: AuthenticationManager,
    currentUserRepository: CurrentUserRepository,
    userStatusRepository: UserStatusRepository,
    otherUserRepository: OtherUserRepository,
    chatRepository: ChatRepository,
    var otherUserId: String?
) : ViewModel() {

    private val _otherUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var otherUserLiveData: LiveData<UserModel> = _otherUserMutableLiveData

    private val _messagesMutableLiveData: MutableLiveData<ArrayList<MessageModel>> =
        MutableLiveData()
    var messagesLiveData: LiveData<ArrayList<MessageModel>> = _messagesMutableLiveData

    private var messagingManager: MessagingManager? = null

    init {
        authenticationManager.currentUserId?.let { currentUserId ->
            otherUserId?.let { otherUserId ->
                viewModelScope.launch {
                    _otherUserMutableLiveData.postValue(otherUserRepository.getOtherUserById(otherUserId))

                    val userModel = currentUserRepository.getCurrentUser(currentUserId)
                    val chatId = chatRepository.findChatById(currentUserId, otherUserId)
                    messagingManager = userModel?.let {
                        MessagingManager(
                            userStatusRepository,
                            chatId,
                            otherUserId,
                            userModel
                        )
                    }
                    _messagesMutableLiveData.postValue(chatRepository.getMessagesFromChat(currentUserId, otherUserId))
                }
            }
        }
    }

    fun sendMessage(message: String) {
        messagingManager?.sendMessage(message)
    }
}