package com.messengerkotlin.fragments.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _currentUserMutableStateFlow: MutableStateFlow<UserModel?> = MutableStateFlow(null)
    var currentUserStateFlow: StateFlow<UserModel?> = _currentUserMutableStateFlow

    init {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                currentUserRepository.getCurrentUser(it).collect { _currentUserMutableStateFlow.value = it}
            }
        }
    }

    fun loadProfileImage(imageUri: Uri, contentResolver: ContentResolver) {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                currentUserRepository.loadProfileImage(
                    it,
                    imageUri,
                    contentResolver
                )
            }
        }
    }
}