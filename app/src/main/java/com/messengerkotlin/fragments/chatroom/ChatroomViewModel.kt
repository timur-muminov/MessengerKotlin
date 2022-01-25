package com.messengerkotlin.fragments.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.firebase_repository.messaging.MessagingManager
import com.messengerkotlin.models.MessageModel
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatroomViewModel(
    private val authenticationManager: AuthenticationManager,
    private val currentUserRepository: CurrentUserRepository,
    private val userStatusRepository: UserStatusRepository,
    private val otherUserRepository: OtherUserRepository,
    private val chatRepository: ChatRepository,
    private val messagingManager: MessagingManager,
    private var otherUserId: String
) : ViewModel() {

    private val _otherUserMutableStateFlow: MutableStateFlow<UserModel?> = MutableStateFlow(null)
    val otherUserStateFlow: StateFlow<UserModel?> = _otherUserMutableStateFlow

    private val _messagesMutableSharedFlow: MutableSharedFlow<List<MessageModel>> =
        MutableSharedFlow(0)
    val messagesSharedFlow: SharedFlow<List<MessageModel>> = _messagesMutableSharedFlow

    init {
        setChat()
    }

    private fun setChat() {
        authenticationManager.currentUserId?.let { currentUserId ->
            viewModelScope.launch {
                launch {
                    _otherUserMutableStateFlow.value = otherUserRepository.getOtherUserById(otherUserId)
                }

                val chatInfoModel = chatRepository.findChatById(currentUserId, otherUserId)

                chatInfoModel.otherUserId.let { otherUserId ->
                    launch {
                        userStatusRepository.getStatus(otherUserId).collect { status ->
                            messagingManager.otherUserStatus = status
                        }
                    }
                    launch {
                        currentUserRepository.getCurrentUser(currentUserId).collect { currentUserModel ->
                            messagingManager.create(currentUserId,chatInfoModel.chatId, otherUserId, currentUserModel)

                        }
                    }
                }

                launch {
                    val messages = chatRepository.getMessagesFromStorageChat(chatInfoModel.chatId)
                    if (messages.size != 0) {
                        messages.removeAt(messages.size - 1)
                    }

                    launch {
                        chatRepository.getLastMessage(chatInfoModel.chatId)
                            .collect { messageModel ->
                                messages.add(messageModel)
                                _messagesMutableSharedFlow.emit(messages)
                            }
                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            messagingManager.sendMessage(message)
        }
    }
}