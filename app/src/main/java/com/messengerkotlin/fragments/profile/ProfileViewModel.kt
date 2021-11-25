package com.messengerkotlin.fragments.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.AuthenticationManager
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authenticationManager: AuthenticationManager,
    private val currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    init {
        authenticationManager.currentUserId?.let {
            viewModelScope.launch {
                _currentUserMutableLiveData.postValue(currentUserRepository.getCurrentUser(it))
            }
        }
    }

    fun loadProfileImage(imageUri: Uri, contentResolver: ContentResolver) {
        authenticationManager.currentUserId?.let {
            currentUserRepository.loadProfileImage(
                it,
                imageUri,
                contentResolver
            )
        }
    }

}