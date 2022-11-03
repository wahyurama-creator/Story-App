package com.way.storyapp.di

import android.app.Application
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.local.room.StoryDatabase
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FactoryModule {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideViewModelFactory(
        repository: Repository,
        app: Application,
        dataStoreRepository: DataStoreRepository,
        storyDatabase: StoryDatabase
    ): ViewModelFactory = ViewModelFactory(
        repository, app, dataStoreRepository, storyDatabase
    )
}