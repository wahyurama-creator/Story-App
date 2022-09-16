package com.way.storyapp.data.remote.model.auth

import com.google.gson.annotations.SerializedName

data class UserRegisterData(
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?,
)
