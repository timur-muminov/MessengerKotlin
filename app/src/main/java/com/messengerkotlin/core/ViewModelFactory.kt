package com.messengerkotlin.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.messengerkotlin.fragments.chatroom.ChatroomViewModel

class ViewModelFactory(private val userId: String): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass == ChatroomViewModel::javaClass){
            return ChatroomViewModel(userId) as T
        }
        return super.create(modelClass)
    }
}