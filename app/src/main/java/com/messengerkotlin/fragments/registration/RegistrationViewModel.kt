package com.messengerkotlin.fragments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.AuthenticationManager

class RegistrationViewModel(private val authenticationManager: AuthenticationManager) : ViewModel() {

    private val _statusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    val statusLiveData: LiveData<CommonStatus> = _statusMutableLiveData

    fun signUp(username: String, email: String, password: String) {
        authenticationManager.signUp(username, email, password, _statusMutableLiveData::postValue)
    }
}