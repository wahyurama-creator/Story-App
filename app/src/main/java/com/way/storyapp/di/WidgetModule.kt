package com.way.storyapp.di

import android.content.Context
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import com.way.storyapp.presentation.ui.component.widget.StackRemoteViewFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {

    @Singleton
    @Provides
    fun provideStackRemoteViewFactory(
        @ApplicationContext context: Context,
        repository: Repository,
        dataStoreRepository: DataStoreRepository
    ): StackRemoteViewFactory = StackRemoteViewFactory(context, repository, dataStoreRepository)
}