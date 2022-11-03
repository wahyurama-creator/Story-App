package com.way.storyapp.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.way.storyapp.data.local.model.RemoteKeys
import com.way.storyapp.data.remote.model.story.Story

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}