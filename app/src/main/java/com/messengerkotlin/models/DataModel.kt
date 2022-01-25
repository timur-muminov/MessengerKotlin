package com.messengerkotlin.models

data class DataModel(
    val receiverId: String,
    val id: String,
    val username: String,
    val imageURL: String? = null,
    val message: String
)
