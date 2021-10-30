package com.messengerkotlin.models

data class UserModel(
    var id: String = "",
    var username: String = "",
    var imageurl: String? = null,
    var lastMessage: String = "",
    var token: String = "",
    var userkey: String? = null
)
