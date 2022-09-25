package com.way.storyapp.data

import com.way.storyapp.data.remote.RemoteDataSource
import javax.inject.Inject

class Repository @Inject constructor(
    val remoteDataSource: RemoteDataSource
)