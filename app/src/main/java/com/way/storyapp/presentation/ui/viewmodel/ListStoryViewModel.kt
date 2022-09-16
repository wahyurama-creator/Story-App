package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.way.storyapp.data.Repository
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ListStoryViewModel @Inject constructor(
    private val repository: Repository,
    val app: Application
) : AndroidViewModel(app) {
    private val _storyResponse: MutableLiveData<Resource<StoryResponse>> = MutableLiveData()
    var storyResponse: LiveData<Resource<StoryResponse>> = _storyResponse

    fun getAllStory(auth: String, queries: Map<String, String>) = viewModelScope.launch {
        getAllStorySafeCall(auth, queries)
    }

    private suspend fun getAllStorySafeCall(auth: String, queries: Map<String, String>) {
        _storyResponse.value = Resource.Loading()
        if (isNetworkAvailable(app)) {
            try {
                val response = repository.remoteDataSource.getAllStory(auth, queries)
                _storyResponse.value = handleStoryResponse(response)
            } catch (e: Exception) {
                _storyResponse.value = Resource.Error(e.message.toString())
            }
        } else {
            _storyResponse.value = Resource.Error("No internet connection")
        }
    }

    private fun handleStoryResponse(response: Response<StoryResponse>): Resource<StoryResponse> {
        when {
            response.message().toString().contains("timeout") -> {
                return Resource.Error("Timeout")
            }
            response.code() == 402 -> {
                return Resource.Error("Unauthorized")
            }
            response.body()!!.listStory.isEmpty() -> {
                return Resource.Error("Story not found")
            }
            response.isSuccessful -> {
                val recipes = response.body()
                return if (recipes != null) {
                    Resource.Success(recipes)
                } else {
                    Resource.Error(response.message())
                }
            }
            else -> return Resource.Error(response.message())
        }
    }


}