package com.messengerkotlin.fragments.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.auth_manager.AuthenticationManager
import com.messengerkotlin.firebase_repository.auth_manager.enums.AuthStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authenticationManager: AuthenticationManager) : ViewModel(){

    private val statusMutableSharedFlow: MutableSharedFlow<AuthStatus> = MutableSharedFlow(0)
    val statusSharedFlow: SharedFlow<AuthStatus> = statusMutableSharedFlow

    fun signIn(email: String, password: String){
        viewModelScope.launch {
           statusMutableSharedFlow.emit(authenticationManager.signIn(email, password))
        }
    }
}