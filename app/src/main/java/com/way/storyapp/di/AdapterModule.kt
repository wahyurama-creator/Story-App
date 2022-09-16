package com.way.storyapp.di

import com.way.storyapp.presentation.ui.fragment.list.adapter.StoryAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdapterModule {

    @Provides
    @Singleton
    fun provideStoryAdapter(): StoryAdapter = StoryAdapter()

}