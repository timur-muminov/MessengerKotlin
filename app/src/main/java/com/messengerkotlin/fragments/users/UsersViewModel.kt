package com.messengerkotlin.fragments.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    authenticationManager: AuthenticationManager,
    currentUserRepository: CurrentUserRepository,
    userStatusRepository: UserStatusRepository,
    val otherUserRepository: OtherUserRepository,
    val chatRepository: ChatRepository
) : ViewModel() {

    private val _currentUserMutableStateFlow: MutableStateFlow<UserModel?> = MutableStateFlow(null)
    var currentUserStateFlow: StateFlow<UserModel?> = _currentUserMutableStateFlow

    private val _otherUsersMutableStateFlow: MutableStateFlow<List<UserModel>?> =
        MutableStateFlow(null)
    var otherUsersStateFlow: StateFlow<List<UserModel>?> = _otherUsersMutableStateFlow

    private val currentList: HashMap<String, UserModel> = HashMap()

    init {
        authenticationManager.currentUserId?.let { it ->
            viewModelScope.launch {
                launch {
                    userStatusRepository.setStatus(it, Status.ONLINE)
                }
                getOtherUsersInfoFromChatsRegister(it)

                launch {
                    currentUserRepository.getCurrentUser(it)
                        .collect { _currentUserMutableStateFlow.emit(it) }
                }

                launch {
                    otherUserRepository.getOtherUsersFromLastChatsRegister(it)
                        .collect { otherUserId ->
                            val otherUserModel = otherUserRepository.getOtherUserById(otherUserId.otherUserId)
                            currentList[otherUserModel.id] = otherUserModel
                            _otherUsersMutableStateFlow.value = currentList.values.toList()
                        }
                }


            }
        }
    }

    private fun getOtherUsersInfoFromChatsRegister(it: String) {
        viewModelScope.launch {
            val chatList: List<ChatInfoModel> = otherUserRepository.getOtherUsersFromStorageChatsRegister(it)

            chatList.forEach { chatInfoModel ->
                launch {
                    val userModel = otherUserRepository.getOtherUserById(chatInfoModel.otherUserId)
                    currentList[userModel.id] = userModel
                    _otherUsersMutableStateFlow.value = currentList.values.toList()
                }
            }


        }
    }

}




