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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StackRemoteViewFactory @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<String>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        CoroutineScope(Dispatchers.IO).launch {
//            val token = dataStoreRepository.readToken()
            val query = HashMap<String, Int>()
            query["page"] = 1
            query["size"] = 10
            query["location"] = 1

            val response = repository.remoteDataSource.getAllStory(
                auth = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXFFdzQtVHlpeVRkRnBnNHEiLCJpYXQiOjE2NjM5OTM2MDN9.dOrzlHfX6E_vG3WojXAeZcZfFhQO-J13Y8keabxCjY4",
                query
            )
            var story = ""
            response.body()!!.listStory.forEach {
                story = it.photoUrl
                mWidgetItems.add(story)
            }
            Log.e("mWidget", mWidgetItems.toString())
        }
    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.item_story_widget)
        val bitmap: Bitmap = Glide.with(mContext)
            .asBitmap()
            .load(mWidgetItems[position])
            .submit(512, 512)
            .get()
        Log.e("BitmapItem", bitmap.toString())
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

}