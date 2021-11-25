package com.messengerkotlin.fragments.find_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.AuthenticationManager
import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class FindUserViewModel(
    private val authenticationManager: AuthenticationManager,
    private val otherUserRepository: OtherUserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {


    private val _findUserCallbackMutableSharedFlow = MutableSharedFlow<String?>(replay = 0)
    val findUserCallbackSharedFlow: SharedFlow<String?> = _findUserCallbackMutableSharedFlow

    fun findUser(userKey: String) {
        authenticationManager.currentUserId?.let { currentUserId ->
            otherUserRepository.getOtherUserIdByUserkey(currentUserId, userKey) { otherUserId ->
                otherUserId?.let {
                    chatRepository.createChats(
                        currentUserId,
                        otherUserId
                    )
                }
                viewModelScope.launch {
                    _findUserCallbackMutableSharedFlow.emit(otherUserId)
                }
            }
        }
    }
}