package com.way.storyapp.presentation.ui.fragment.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.data.remote.model.story.StoryResponse
import com.way.storyapp.databinding.ItemStoryBinding
import com.way.storyapp.presentation.ui.fragment.list.ListStoryFragmentDirections
import javax.inject.Inject

class StoryAdapter @Inject constructor() : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private var oldStory = emptyList<Story>()

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.ivStory.load(data.photoUrl) {
                crossfade(true)
                crossfade(500)
            }
            binding.tvStoryName.text = data.name
            binding.tvDescription.text = data.description
            binding.tvDate.text = data.createdAt

            binding.root.setOnClickListener {
                val action =
                    ListStoryFragmentDirections.actionListStoryFragmentToDetailFragment(data)
                binding.root.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = oldStory.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(oldStory[position])
    }

    fun setData(newStory: StoryResponse) {
        val diffUtil = AdapterDiffUtil(oldStory, newStory.listStory)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldStory = newStory.listStory
        diffResults.dispatchUpdatesTo(this)
    }
}