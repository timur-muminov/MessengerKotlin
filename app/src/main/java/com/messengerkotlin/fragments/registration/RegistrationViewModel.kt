package com.messengerkotlin.fragments.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.AuthenticationManager

class RegistrationViewModel : ViewModel() {

    private val _statusMutableLiveData: MutableLiveData<CommonStatus> = MutableLiveData()
    val statusLiveData: LiveData<CommonStatus> = _statusMutableLiveData

    private val authManager = AuthenticationManager()

    fun signUp(username: String, email: String, password: String) {
        authManager.signUp(username, email, password, _statusMutableLiveData::postValue)
    }
}