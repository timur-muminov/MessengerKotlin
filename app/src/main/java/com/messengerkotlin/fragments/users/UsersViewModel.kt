package com.messengerkotlin.fragments.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.models.UserModel

class UsersViewModel : ViewModel() {

    private val _currentUserMutableLiveData: MutableLiveData<UserModel> = MutableLiveData()
    var currentUserLiveData: LiveData<UserModel> = _currentUserMutableLiveData

    private val _otherUsersMutableLiveData: MutableLiveData<List<UserModel>> = MutableLiveData()
    var otherUsersLiveData: LiveData<List<UserModel>> = _otherUsersMutableLiveData

    init {
        UserManager.firebaseRepository?.setStatus(Status.ONLINE)
        UserManager.firebaseRepository?.getCurrentUserInfo { _currentUserMutableLiveData.postValue(it) }
        UserManager.firebaseRepository?.getOtherUsersInfoFromChatsRegister { _otherUsersMutableLiveData.postValue(it) }
    }


}