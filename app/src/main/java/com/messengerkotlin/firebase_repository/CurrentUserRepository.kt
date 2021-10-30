package com.messengerkotlin.firebase_repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.messengerkotlin.core.LoadingState
import com.messengerkotlin.models.UserModel

class CurrentUserRepository(
    private val authenticationManager: AuthenticationManager,
    private val usersRepository: UsersRepository
) {

    fun getCurrentUserLiveData() =
        Transformations.switchMap(authenticationManager.getCurrentUserIdLoadingState()) {
            when (it) {
                is LoadingState.Loaded -> if (it.data == null) {
                    MutableLiveData(LoadingState.Loaded<UserModel?>(null))
                } else {
                    usersRepository.getUserLiveData(it.data)
                }
                is LoadingState.Loading -> MutableLiveData(LoadingState.Loading())
                is LoadingState.Error -> error("illegal")
            }
        }
}