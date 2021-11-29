package com.messengerkotlin.fragments.name_editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.AuthenticationManager
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.launch

class NameEditorViewModel(
    var authenticationManager: AuthenticationManager,
    var currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel?> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel?> = _currentUserMutableLiveData

    private val _editUserKeyStatusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    var editUserKeyStatusLiveData: LiveData<CommonStatus> = _editUserKeyStatusMutableLiveData

    private var currentUserModel: UserModel? = null

    init {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                currentUserRepository.getCurrentUser(it) { _currentUserMutableLiveData.postValue(it) }
            }
        }
    }

    suspend fun editProfile(username: String, userkey: String) {
        if (username != currentUserModel?.username) {
            authenticationManager.currentUserId?.let {
                currentUserRepository.editUsername(
                    it,
                    username
                )
            }
        }
        if (userkey != currentUserModel?.userkey) {
            _editUserKeyStatusMutableLiveData.postValue(currentUserRepository.editUserKey(userkey))
        }
    }
}