package com.way.storyapp.presentation.ui.component.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoryWidgetService : RemoteViewsService() {

    @Inject
    lateinit var stackRemoteViewFactory: StackRemoteViewFactory

    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory = stackRemoteViewFactory
}