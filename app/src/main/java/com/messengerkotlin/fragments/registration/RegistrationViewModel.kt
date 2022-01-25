package com.messengerkotlin.fragments.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.firebase_repository.auth_manager.enums.AuthStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val authenticationManager: AuthenticationManager) : ViewModel() {

    private val statusMutableSharedFlow: MutableSharedFlow<AuthStatus> = MutableSharedFlow(0)
    val statusSharedFlow: SharedFlow<AuthStatus> = statusMutableSharedFlow

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            statusMutableSharedFlow.emit(authenticationManager.signUp(username, email, password))
        }
    }
}