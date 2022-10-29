package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: Repository,
    private val app: Application,
    dataStoreRepository: DataStoreRepository
) : AndroidViewModel(app) {

    private val _storyResponse: MutableLiveData<Resource<StoryResponse>> = MutableLiveData()
    var storyResponse: LiveData<Resource<StoryResponse>> = _storyResponse

    val readToken: LiveData<String> = dataStoreRepository.readToken().asLiveData()

    fun getAllStoryLocation(auth: String, queries: Map<String, Int>) = viewModelScope.launch {
        getAllStorySafeCall(auth, queries)
    }

    private suspend fun getAllStorySafeCall(auth: String, queries: Map<String, Int>) {
        _storyResponse.value = Resource.Loading()
        if (isNetworkAvailable(app)) {
            try {
                val response = repository.remoteDataSource.getAllStory(auth, queries)
//                _storyResponse.value = handleStoryResponse(response)
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