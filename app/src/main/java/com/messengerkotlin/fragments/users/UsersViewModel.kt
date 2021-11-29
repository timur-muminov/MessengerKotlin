package com.messengerkotlin.fragments.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.*
import com.messengerkotlin.models.MessageModel
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UsersViewModel(
    private var authenticationManager: AuthenticationManager,
    currentUserRepository: CurrentUserRepository,
    userStatusRepository: UserStatusRepository,
    otherUserRepository: OtherUserRepository,
    private var chatRepository: ChatRepository
) : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    private val _otherUsersMutableLiveData: MutableLiveData<List<UserModel>> = MutableLiveData()
    var otherUsersLiveData: LiveData<List<UserModel>> = _otherUsersMutableLiveData

    init {
        authenticationManager.currentUserId?.let { it ->
            userStatusRepository.setStatus(
                it,
                Status.ONLINE
            )

            viewModelScope.launch {
                currentUserRepository.getCurrentUser(it){_currentUserMutableLiveData.postValue(it)}
                map(otherUserRepository.getOtherUsersInfoFromChatsRegister(it)){_otherUsersMutableLiveData.postValue(it)}
            }
        }
    }

    private suspend fun map(
        mapUsers: HashMap<String, UserModel>,
        callback: (List<UserModel>?) -> Unit
    ) {
        authenticationManager.currentUserId?.let { currentUserId ->
            var count = 0
            mapUsers.forEach { map ->
                val chatId = chatRepository.findChatById(currentUserId, map.key)
                chatRepository.getActualMessage(chatId) { messageModel ->
                    map.value.lastMessage = messageModel?.message ?: ""
                    if (mapUsers.size == count) {
                        callback(ArrayList(mapUsers.values))
                    }
                    count++
                }

            }
        }
    }

}




