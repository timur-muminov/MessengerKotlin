package com.messengerkotlin.fragments.find_user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindUserViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val otherUserRepository: OtherUserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {


    private val _findUserCallbackMutableSharedFlow = MutableSharedFlow<String?>(replay = 0)
    val findUserCallbackSharedFlow: SharedFlow<String?> = _findUserCallbackMutableSharedFlow

    fun findUser(userKey: String) {
        authenticationManager.currentUserId?.let { currentUserId ->
            viewModelScope.launch {
                val otherUserId = otherUserRepository.getOtherUserIdByUserkey(userKey)
                otherUserId?.let {
                    chatRepository.createChats(
                        currentUserId,
                        otherUserId
                    )
                }
                _findUserCallbackMutableSharedFlow.emit(otherUserId)
            }
        }
    }
}