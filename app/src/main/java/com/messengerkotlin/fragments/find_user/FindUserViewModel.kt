package com.messengerkotlin.fragments.find_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FindUserViewModel : ViewModel() {

    private val findUserStatusMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val findUserStatusLiveData: LiveData<String> = findUserStatusMutableLiveData

    fun findUser(userKey: String) {
        UserManager.firebaseRepository?.findUser(userKey) { findUserStatusMutableLiveData.postValue(it) }
    }
}