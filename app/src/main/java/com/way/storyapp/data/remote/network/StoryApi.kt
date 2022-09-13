package com.way.storyapp.data.remote.network

import com.way.storyapp.data.remote.model.auth.LoginResponse
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.data.remote.model.auth.UserData
import com.way.storyapp.data.remote.model.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryApi {

    @POST("/register")
    suspend fun register(
        @Body userData: UserData
    ): Response<PostResponse>

    @POST("/login")
    suspend fun login(
        @Body userData: UserData
    ): Response<LoginResponse>

    @Headers("Content-Type: multipart/form-data")
    @Multipart
    @POST("/stories")
    suspend fun postStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @PartMap parts: Map<String, RequestBody>,
    ): Response<PostResponse>

    @GET("/stories")
    suspend fun getAllStory(
        @Header("Authorization") auth: String,
        @QueryMap queries: Map<String, String>,
    ): Response<StoryResponse>
}