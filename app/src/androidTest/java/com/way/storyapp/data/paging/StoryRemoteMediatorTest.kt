package com.way.storyapp.data.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.way.storyapp.data.local.datastore.DataStoreRepository
import com.way.storyapp.data.local.room.StoryDatabase
import com.way.storyapp.data.remote.model.auth.LoginResponse
import com.way.storyapp.data.remote.model.auth.LoginResult
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.data.remote.model.auth.UserRegisterData
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.data.remote.network.StoryApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: StoryApi = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    private val dataStoreRepository = DataStoreRepository(ApplicationProvider.getApplicationContext())

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    suspend fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent(){
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            dataStoreRepository
        )
        val pagingState = PagingState<Int, Story>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

}

class FakeApiService : StoryApi {

    override suspend fun register(userRegisterData: UserRegisterData): Response<PostResponse> {
        return Response.success(PostResponse(false, ""))
    }

    override suspend fun login(userRegisterData: UserRegisterData): Response<LoginResponse> {
        return Response.success(
            LoginResponse(
                false, LoginResult(
                    "", "", ""
                ), ""
            )
        )
    }

    override suspend fun postStory(
        auth: String,
        file: MultipartBody.Part,
        parts: HashMap<String, RequestBody>
    ): Response<PostResponse> {
        return Response.success(PostResponse(false, ""))
    }


    override suspend fun getAllStory(auth: String, queries: Map<String, Int>): StoryResponse {
        val item: MutableList<Story> = arrayListOf()

        val userToken = "Bearer "
        val queryParam = HashMap<String, Int>()
        queryParam["page"] = 1
        queryParam["size"] = 15
        queryParam["location"] = 0

//        val page = queryParam["page"]!!
//        val size = queryParam["size"]!!

        for (i in 0..15) {
            val story = Story(
                id = i.toString(),
                name = "Story $i",
                photoUrl = "photo-url-$i",
                lon = i.toDouble(),
                lat = i.toDouble(),
                description = "Description story $i",
                createdAt = "${LocalDateTime.now()} + $i"
            )
            item.add(story)
        }
//        val result = item.subList((page - 1) * size, (page - 1) * size + size)
        return StoryResponse(
            false, item, ""
        )
    }

}