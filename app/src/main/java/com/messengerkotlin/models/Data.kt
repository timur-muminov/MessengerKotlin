package com.messengerkotlin.models

data class Data(
    val receiverId: String,
    val id: String,
    val username: String,
    val imageURL: String? = null,
    val message: String
)
