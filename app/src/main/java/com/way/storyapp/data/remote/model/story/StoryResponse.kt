package com.way.storyapp.data.remote.model.story

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: List<Story>,
    @SerializedName("message")
    val message: String
)