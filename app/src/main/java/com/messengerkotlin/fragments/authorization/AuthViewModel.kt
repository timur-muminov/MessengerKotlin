package com.messengerkotlin.fragments.authorization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.AuthenticationManager

class AuthViewModel(private val authenticationManager: AuthenticationManager) : ViewModel(){

    private val _statusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    var statusLiveData: LiveData<CommonStatus> = _statusMutableLiveData

    fun signIn(email: String, password: String){
        authenticationManager.signIn(email, password, _statusMutableLiveData::postValue)
    }
}