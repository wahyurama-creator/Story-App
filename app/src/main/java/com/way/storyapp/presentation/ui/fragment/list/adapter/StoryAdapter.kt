package com.way.storyapp.presentation.ui.fragment.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.databinding.ItemStoryBinding
import com.way.storyapp.presentation.ui.fragment.list.ListStoryFragmentDirections
import com.way.storyapp.presentation.ui.utils.formatDateToString
import javax.inject.Inject

class StoryAdapter @Inject constructor() :
    PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.ivStory.load(data.photoUrl)
            binding.tvStoryName.text = data.name
            binding.tvDescription.text = data.description
            binding.tvDate.text = formatDateToString(data.createdAt)

            binding.root.setOnClickListener {
                val action =
                    ListStoryFragmentDirections.actionListStoryFragmentToDetailFragment(data)
                binding.root.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}