package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.local.room.StoryDatabase
import com.way.storyapp.data.paging.StoryPagingSource
import com.way.storyapp.data.paging.StoryRemoteMediator
import com.way.storyapp.data.remote.model.story.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class ListStoryViewModel @Inject constructor(
    private val repository: Repository,
    val app: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val storyDatabase: StoryDatabase
) : AndroidViewModel(app) {

    val story: LiveData<PagingData<Story>> = getAllStory().cachedIn(viewModelScope)

    fun getAllStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                repository.remoteDataSource.storyApi,
                dataStoreRepository
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}