package com.way.storyapp.data.local.model

data class UserModel(
    val name: String,
    val stateLogin: Boolean = false,
    val token: String
)