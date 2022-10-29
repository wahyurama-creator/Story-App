package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import com.way.storyapp.data.paging.StoryPagingSource
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.data.remote.network.StoryApi
import com.way.storyapp.presentation.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ListStoryViewModel @Inject constructor(
    private val repository: Repository,
    val app: Application,
    private val dataStoreRepository: DataStoreRepository,
) : AndroidViewModel(app) {
//    private val _storyResponse: MutableLiveData<Resource<StoryResponse>> = MutableLiveData()
//    var storyResponse: LiveData<Resource<StoryResponse>> = _storyResponse
//
//    val readToken: LiveData<String> = dataStoreRepository.readToken().asLiveData()

    val story: LiveData<PagingData<Story>> = getAllStory().cachedIn(viewModelScope)

    private fun getAllStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPagingSource(
                    api = repository.remoteDataSource.storyApi,
                    dataStoreRepository = dataStoreRepository
                )
            }
        ).liveData
    }

//    fun getAllStory(auth: String, queries: Map<String, Int>) = viewModelScope.launch {
//        getAllStorySafeCall(auth, queries)
//    }

//    private suspend fun getAllStorySafeCall(auth: String, queries: Map<String, Int>) {
//        _storyResponse.value = Resource.Loading()
//        if (isNetworkAvailable(app)) {
//            try {
//                val response = repository.remoteDataSource.getAllStory(auth, queries)
//                _storyResponse.value = handleStoryResponse(response)
//            } catch (e: Exception) {
//                _storyResponse.value = Resource.Error(e.message.toString())
//            }
//        } else {
//            _storyResponse.value = Resource.Error("No internet connection")
//        }
//    }

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

    fun setQueryParam(): HashMap<String, Int> {
        val query = HashMap<String, Int>()
        query["page"] = 1
        query["size"] = 20
        query["location"] = 0
        return query
    }
}