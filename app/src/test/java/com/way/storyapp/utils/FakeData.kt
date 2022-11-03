package com.way.storyapp.utils

import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.data.remote.model.story.StoryResponse
import java.time.LocalDateTime

object FakeData {

    fun generateFakeStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = i.toString(),
                name = "Story $i",
                photoUrl = "photo-url-$i",
                lon = i.toDouble(),
                lat = i.toDouble(),
                description = "Description story $i",
                createdAt = "${LocalDateTime.now()} + $i"
            )
            items.add(story)
        }
        return items
    }

    fun generateFakeStory(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponse(
                error = false,
                listStory = listOf(
                    Story(
                        id = i.toString(),
                        name = "Story $i",
                        photoUrl = "photo-url-$i",
                        lon = i.toDouble(),
                        lat = i.toDouble(),
                        description = "Description story $i",
                        createdAt = "${LocalDateTime.now()} + $i"
                    )
                ),
                message = ""
            )
            items.add(story)
        }
        return items
    }

}