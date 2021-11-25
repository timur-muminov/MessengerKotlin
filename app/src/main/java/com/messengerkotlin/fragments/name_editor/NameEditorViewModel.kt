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

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    private val _editUserKeyStatusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    var editUserKeyStatusLiveData: LiveData<CommonStatus> = _editUserKeyStatusMutableLiveData

    private var currentUserModel: UserModel? = null

    init {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                currentUserModel = currentUserRepository.getCurrentUser(it)
                _currentUserMutableLiveData.postValue(currentUserModel)
            }
        }
    }

    fun editProfile(username: String, userkey: String) {
        if (username != currentUserModel?.username) {
            authenticationManager.currentUserId?.let {
                currentUserRepository.editUsername(
                    it,
                    username
                )
            }
        }
        if (userkey != currentUserModel?.userkey) {
            authenticationManager.currentUserId?.let { it1 ->
                currentUserRepository.editUserKey(
                    it1,
                    userkey
                ) { it2 -> _editUserKeyStatusMutableLiveData.postValue(it2) }
            }
        }
    }
}