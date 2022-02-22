package com.messengerkotlin.fragments.name_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameEditorViewModel @Inject constructor(
    private var authenticationManager: AuthenticationManager,
    private var currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _currentUserMutableStateFlow: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())
    var currentUserStateFlow: StateFlow<UserModel> = _currentUserMutableStateFlow

    private val _editUserKeyStatusMutableStateFlow: MutableStateFlow<CommonStatus?> =
        MutableStateFlow(null)
    var editUserKeyStatusStateFlow: StateFlow<CommonStatus?> = _editUserKeyStatusMutableStateFlow

    private var currentUserModel: UserModel? = null

    init {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                currentUserRepository.getCurrentUser(it)
                    .collect { _currentUserMutableStateFlow.emit(it) }
            }
        }
    }

    fun editProfile(username: String, userkey: String) {
        authenticationManager.currentUserId?.let {
            if (username != currentUserModel?.username) {
                viewModelScope.launch {
                    currentUserRepository.editUsername(it, username)
                }
            }

            if (userkey != currentUserModel?.userkey) {
                viewModelScope.launch {
                    _editUserKeyStatusMutableStateFlow.emit(currentUserRepository.editUserKey(it, userkey))
                }
            }
        }
    }
}