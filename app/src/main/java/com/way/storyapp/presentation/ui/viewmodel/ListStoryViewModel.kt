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
}