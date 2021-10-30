package com.messengerkotlin.fragments.profile.name_editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.models.UserModel

class NameEditorViewModel : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    private val _editUserKeyStatusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    var editUserKeyStatusLiveData: LiveData<CommonStatus> = _editUserKeyStatusMutableLiveData

    private lateinit var currentUserModel: UserModel

    init {
        UserManager.firebaseRepository?.getCurrentUserInfo { userModel ->
            currentUserModel = userModel
            _currentUserMutableLiveData.postValue(userModel)
        }
    }

    fun editProfile(username: String, userkey: String) {
        if (username != currentUserModel.username) {
            UserManager.firebaseRepository?.changeUsername(username)
        }
        if (userkey != currentUserModel.userkey) {
            UserManager.firebaseRepository?.changeUserkey(userkey) { _editUserKeyStatusMutableLiveData.postValue(it) }
        }
    }


}