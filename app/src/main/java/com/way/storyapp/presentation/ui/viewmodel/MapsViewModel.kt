package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                _storyResponse.value = handleStoryResponse(response)
            } catch (e: Exception) {
                _storyResponse.value = Resource.Error(e.message.toString())
            }
        } else {
            _storyResponse.value = Resource.Error("No internet connection")
        }
    }

    private fun handleStoryResponse(response: StoryResponse): Resource<StoryResponse> {
        return when {
            response.message.contains("timeout") -> {
                Resource.Error("Timeout")
            }
            response.listStory.isEmpty() -> {
                Resource.Error("Story not found")
            }
            !response.error || response.message == "Stories fetched successfully" -> {
                Resource.Success(response)
            }
            else -> Resource.Error(response.message)
        }
    }
}