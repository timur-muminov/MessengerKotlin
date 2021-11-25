package com.messengerkotlin.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.messengerkotlin.App
import com.messengerkotlin.firebase_repository.ChatRepository
import com.messengerkotlin.firebase_repository.CurrentUserRepository
import com.messengerkotlin.firebase_repository.OtherUserRepository
import com.messengerkotlin.firebase_repository.UserStatusRepository
import com.messengerkotlin.fragments.authorization.AuthViewModel
import com.messengerkotlin.fragments.chatroom.ChatroomViewModel
import com.messengerkotlin.fragments.profile.ProfileViewModel
import com.messengerkotlin.fragments.registration.RegistrationViewModel
import com.messengerkotlin.fragments.users.UsersViewModel
import com.messengerkotlin.fragments.find_user.FindUserViewModel
import com.messengerkotlin.fragments.name_editor.NameEditorViewModel

class ViewModelFactory(private val userId: String?) : ViewModelProvider.Factory {

    private val currentUserRepository: CurrentUserRepository = CurrentUserRepository()
    private val userStatusRepository: UserStatusRepository = App.instance.userStatusRepository
    private val otherUserRepository: OtherUserRepository = OtherUserRepository()
    private val chatRepository: ChatRepository = ChatRepository()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom( ChatroomViewModel::class.java) -> {
                ChatroomViewModel(
                    App.instance.authenticationManager,
                    currentUserRepository,
                    userStatusRepository,
                    otherUserRepository,
                    chatRepository,
                    userId
                ) as T
            }
            modelClass.isAssignableFrom( FindUserViewModel::class.java) -> {
                FindUserViewModel(App.instance.authenticationManager, otherUserRepository, chatRepository) as T
            }

            modelClass.isAssignableFrom( UsersViewModel::class.java) -> {
                UsersViewModel(
                    App.instance.authenticationManager,
                    currentUserRepository,
                    userStatusRepository,
                    otherUserRepository,
                    chatRepository
                ) as T
            }

            modelClass.isAssignableFrom( ProfileViewModel::class.java) ->{
                ProfileViewModel(
                    App.instance.authenticationManager,
                    currentUserRepository
                ) as T
            }

            modelClass.isAssignableFrom( RegistrationViewModel::class.java) -> {
                RegistrationViewModel(App.instance.authenticationManager) as T
            }

            modelClass.isAssignableFrom( AuthViewModel::class.java) -> {
                AuthViewModel(App.instance.authenticationManager) as T
            }

            modelClass.isAssignableFrom( NameEditorViewModel::class.java) -> {
                NameEditorViewModel(App.instance.authenticationManager,currentUserRepository) as T
            }

            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}