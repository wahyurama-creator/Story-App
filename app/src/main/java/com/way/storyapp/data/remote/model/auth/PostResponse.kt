package com.way.storyapp.data.remote.model.auth

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)