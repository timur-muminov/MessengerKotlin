package com.messengerkotlin.fragments.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.firebase_repository.*
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
                _currentUserMutableLiveData.postValue(currentUserRepository.getCurrentUser(it))
                _otherUsersMutableLiveData.postValue(map(otherUserRepository.getOtherUsersInfoFromChatsRegister(it)))
            }
        }
    }

    private suspend fun map(mapUsers: HashMap<String, UserModel>): List<UserModel>? {
        authenticationManager.currentUserId?.let { currentUserId ->
            mapUsers.forEach {
                val messageModel = chatRepository.getLastMessage(currentUserId, it.key)
                it.value.lastMessage = messageModel?.message ?: ""
            }
            return ArrayList(mapUsers.values)
        }
        return null
    }
}




