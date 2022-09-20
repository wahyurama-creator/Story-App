package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: Repository,
    private val app: Application,
    dataStoreRepository: DataStoreRepository
) : AndroidViewModel(app) {
    private var _postStory: MutableLiveData<Resource<PostResponse>> = MutableLiveData()
    val postStory: LiveData<Resource<PostResponse>> = _postStory

    val readToken: LiveData<String> = dataStoreRepository.readToken().asLiveData()

    fun postStory(
        auth: String,
        file: MultipartBody.Part,
        parts: HashMap<String, RequestBody>
    ) = viewModelScope.launch {
        postStorySafeCall(auth, file, parts)
    }

    private suspend fun postStorySafeCall(
        auth: String,
        file: MultipartBody.Part,
        parts: HashMap<String, RequestBody>
    ) {
        _postStory.value = Resource.Loading()
        if (isNetworkAvailable(app)) {
            try {
                val response = repository.remoteDataSource.postStory(auth, file, parts)
                _postStory.value = handlePostStoryResponse(response)
            } catch (e: IOException) {
                _postStory.value = Resource.Error(e.message.toString())
            }
        } else {
            _postStory.value = Resource.Error("No internet connection")
        }
    }

    private fun handlePostStoryResponse(response: Response<PostResponse>): Resource<PostResponse> {
        when {
            response.isSuccessful -> {
                val data = response.body()
                return if (data != null) {
                    Resource.Success(data)
                } else {
                    Resource.Error(response.errorBody().toString())
                }
            }
            !response.isSuccessful -> {
                val messageErr =
                    Gson().fromJson(response.errorBody()!!.charStream(), PostResponse::class.java)
                return Resource.Error(messageErr.message)
            }
            else -> return Resource.Error(response.errorBody().toString())
        }
    }

    fun setMapBody(description: String, lat: Double, lon: Double): HashMap<String, RequestBody> {
        val body = HashMap<String, RequestBody>()
        body["description"] = description.toRequestBody("text/plain".toMediaTypeOrNull())
        body["lat"] = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        body["lon"] = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return body
    }
}