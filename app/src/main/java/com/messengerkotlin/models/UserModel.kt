package com.messengerkotlin.models

data class UserModel(
    var id: String = "",
    var username: String = "",
    var imageurl: String? = null,
    var token: String = "",
    var userkey: String? = ""
)
