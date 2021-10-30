package com.messengerkotlin.fragments.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.models.UserModel

class ProfileViewModel : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    init {
        UserManager.firebaseRepository?.getCurrentUserInfo { _currentUserMutableLiveData.postValue(it) }
    }

    fun loadProfileImage(imageUri: Uri, contentResolver: ContentResolver){
        UserManager.firebaseRepository?.loadProfileImage(imageUri, contentResolver)
    }

}