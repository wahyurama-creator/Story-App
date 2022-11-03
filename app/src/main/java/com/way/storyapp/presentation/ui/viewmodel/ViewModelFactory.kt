package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.local.room.StoryDatabase
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val repository: Repository,
    val app: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val storyDatabase: StoryDatabase
) : ViewModelProvider.Factory {

    @ExperimentalPagingApi
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(repository, app, dataStoreRepository, storyDatabase) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, app, dataStoreRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository, app, dataStoreRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository, app, dataStoreRepository) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }
    }

}