package com.way.storyapp.presentation.ui.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.way.storyapp.databinding.FragmentListStoryBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.fragment.list.adapter.LoadingStateAdapter
import com.way.storyapp.presentation.ui.fragment.list.adapter.StoryAdapter
import com.way.storyapp.presentation.ui.viewmodel.ListStoryViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var listStoryViewModel: ListStoryViewModel
    private lateinit var factory: ViewModelFactory
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(layoutInflater, container, false)

        storyAdapter = (activity as MainActivity).storyAdapter
        factory = (activity as MainActivity).factory
        listStoryViewModel = ViewModelProvider(this, factory)[ListStoryViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnBackPressed()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            viewLifecycleOwner.lifecycleScope.launch {
                adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { storyAdapter.retry() }
                )

                listStoryViewModel.story.observe(viewLifecycleOwner) {
                    storyAdapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun handleOnBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listStoryViewModel.story.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = ListStoryFragment::class.java.simpleName
    }
}