package com.way.storyapp.data.remote

import com.way.storyapp.data.remote.model.auth.LoginResponse
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.data.remote.model.auth.UserData
import com.way.storyapp.data.remote.network.StoryApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val storyApi: StoryApi) {
    suspend fun register(userData: UserData): Response<PostResponse> =
        storyApi.register(userData)

    suspend fun login(userData: UserData): Response<LoginResponse> =
        storyApi.login(userData)

    suspend fun postStory(auth: String, file: MultipartBody.Part, parts: Map<String, RequestBody>) =
        storyApi.postStory(auth, file, parts)

    suspend fun getAllStory(auth: String, queries: Map<String, String>) =
        storyApi.getAllStory(auth, queries)
}