package com.messengerkotlin.fragments.authorization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.AuthenticationManager

class AuthViewModel : ViewModel(){

    private val _statusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    var statusLiveData: LiveData<CommonStatus> = _statusMutableLiveData

    private val authManager = AuthenticationManager()

    fun signIn(email: String, password: String){
        authManager.signIn(email, password, _statusMutableLiveData::postValue)
    }
}