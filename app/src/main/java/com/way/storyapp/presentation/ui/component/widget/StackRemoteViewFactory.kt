package com.way.storyapp.presentation.ui.component.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.way.storyapp.R
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class StackRemoteViewFactory @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<String>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        runBlocking {
            val query = HashMap<String, Int>()
            query["page"] = 1
            query["size"] = 10
            query["location"] = 1

            /* Saya ingin bertanya, ketika saya mengambil data token lalu saya salurkan
            * ke get API. Widget selalu empty, padahal waktu saya log itu listWidgetnya
            * ada isinya. Apakah Anda dapat membantu saya?
            * */

            // val tokenAuth = dataStoreRepository.readToken().collect { }

            val response = repository.remoteDataSource.getAllStory(
                "Bearer $BEARER_TOKEN",
                query
            )
            when {
                response.isSuccessful -> {
                    response.body()?.listStory?.map {
                        mWidgetItems.add(it.photoUrl)
                    }
                }
                else -> {
                    Log.e(RESPONSE_ERROR, "Response Error")
                }
            }
        }

    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = 10

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.item_story_widget)

        val bitmap: Bitmap = Glide.with(mContext.applicationContext)
            .asBitmap()
            .load(mWidgetItems[position])
            .submit()
            .get()
        rv.setImageViewBitmap(R.id.ivStoryWidget, bitmap)

        val extras = bundleOf(
            StoryAppWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent().putExtras(extras)

        rv.setOnClickFillInIntent(R.id.ivStoryWidget, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    companion object {
        private const val RESPONSE_ERROR = "RESPONSE_ERROR"
        private const val BEARER_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXFFdzQtVHlpeVRkRnBnNHEiLCJpYXQiOjE2NjM2ODQ2MzJ9.P5Acm7UFLRMvyTcKBfEvAYpqHQJQsq9E_MsR5u2xo8w"
    }
}