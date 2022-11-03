package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.local.room.StoryDatabase
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.presentation.ui.fragment.list.adapter.StoryAdapter
import com.way.storyapp.presentation.ui.viewmodel.StoryPagingSource.Companion.snapshot
import com.way.storyapp.utils.FakeData
import com.way.storyapp.utils.MainDispatcherRule
import com.way.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var dataStoreRepository: DataStoreRepository

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var app: Application

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val fakeStory = FakeData.generateFakeStoryResponse()
        val data: PagingData<Story> = snapshot(fakeStory)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data

        val listStoryViewModel = ListStoryViewModel(
            repository,
            app,
            dataStoreRepository,
            storyDatabase
        )
        Mockito.`when`(listStoryViewModel.getAllStory()).thenReturn(expectedStory)

        val actualStory: PagingData<Story> = listStoryViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(fakeStory, differ.snapshot())
        assertEquals(fakeStory.size, differ.snapshot().size)
        assertEquals(fakeStory, differ.snapshot()[0]?.name)
    }

}


sealed class StoryPagingSource : PagingSource<Int, LiveData<Story>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<Story>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<Story>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

private val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}