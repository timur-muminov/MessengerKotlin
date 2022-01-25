package com.messengerkotlin.di

import com.messengerkotlin.fragments.authorization.AuthViewModel
import com.messengerkotlin.fragments.chatroom.ChatroomViewModel
import com.messengerkotlin.fragments.find_user.FindUserViewModel
import com.messengerkotlin.fragments.name_editor.NameEditorViewModel
import com.messengerkotlin.fragments.profile.ProfileViewModel
import com.messengerkotlin.fragments.registration.RegistrationViewModel
import com.messengerkotlin.fragments.users.UsersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModel = module {

    viewModel<AuthViewModel> { AuthViewModel(authenticationManager = get()) }
    viewModel<RegistrationViewModel> { RegistrationViewModel(authenticationManager = get()) }
    viewModel<UsersViewModel> {
        UsersViewModel(
            authenticationManager = get(),
            currentUserRepository = get(),
            userStatusRepository = get(),
            otherUserRepository = get(),
            chatRepository = get()
        )
    }
    viewModel<ProfileViewModel> {
        ProfileViewModel(
            authenticationManager = get(),
            currentUserRepository = get()
        )
    }
    viewModel<NameEditorViewModel> {
        NameEditorViewModel(
            authenticationManager = get(),
            currentUserRepository = get()
        )
    }
    viewModel<FindUserViewModel> {
        FindUserViewModel(
            authenticationManager = get(),
            otherUserRepository = get(),
            chatRepository = get()
        )
    }
    viewModel<ChatroomViewModel> { parameters ->
        ChatroomViewModel(
            authenticationManager = get(),
            currentUserRepository = get(),
            userStatusRepository = get(),
            otherUserRepository = get(),
            chatRepository = get(),
            messagingManager = get(),
            otherUserId = parameters.get()
        )
    }
}