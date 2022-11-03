package com.way.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.data.remote.network.StoryApi
import kotlinx.coroutines.flow.first

class StoryPagingSource (
    private val api: StoryApi,
    private val dataStoreRepository: DataStoreRepository
) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val userToken = "Bearer ${dataStoreRepository.readToken().first()}"
            val queryParam = HashMap<String, Int>()
            queryParam["page"] = position
            queryParam["size"] = params.loadSize
            queryParam["location"] = 0

            val responseData = api.getAllStory(userToken, queryParam)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) { return LoadResult.Error(e) }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}