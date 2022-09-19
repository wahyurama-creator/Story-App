package com.way.storyapp.data.remote

import com.way.storyapp.data.remote.model.auth.LoginResponse
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.data.remote.model.auth.UserRegisterData
import com.way.storyapp.data.remote.network.StoryApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val storyApi: StoryApi) {
    suspend fun register(userRegisterData: UserRegisterData): Response<PostResponse> =
        storyApi.register(userRegisterData)

    suspend fun login(userRegisterData: UserRegisterData): Response<LoginResponse> =
        storyApi.login(userRegisterData)

    suspend fun postStory(auth: String, file: MultipartBody.Part, parts: HashMap<String, RequestBody>) =
        storyApi.postStory(auth, file, parts)

    suspend fun getAllStory(auth: String, queries: Map<String, Int>) =
        storyApi.getAllStory(auth, queries)
}