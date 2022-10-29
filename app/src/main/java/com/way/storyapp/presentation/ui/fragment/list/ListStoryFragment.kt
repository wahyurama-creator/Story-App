package com.way.storyapp.presentation.ui.fragment.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.way.storyapp.databinding.FragmentListStoryBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.fragment.list.adapter.StoryAdapter
import com.way.storyapp.presentation.ui.viewmodel.ListStoryViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory

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

        getStory()

//        listStoryViewModel.readToken.observe(viewLifecycleOwner) {
//            getAllStory("Bearer $it")
//        }
    }

    private fun getStory() {
        listStoryViewModel.story.observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
            Log.e(TAG, storyAdapter.itemCount.toString())
        }
    }

//    private fun getAllStory(token: String) {
//        showLoading(true)
//        listStoryViewModel.getAllStory(token, listStoryViewModel.setQueryParam())
//        listStoryViewModel.storyResponse.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is Resource.Success -> {
//                    showLoading(false)
//                    response.data?.let {
//                        storyAdapter.setData(it)
//                    }
//                }
//                is Resource.Error -> {
//                    showLoading(false)
//                    Toast.makeText(
//                        requireContext(),
//                        response.message.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.e("ERROR", response.message.toString())
//                }
//                is Resource.Loading -> {
//                    showLoading(true)
//                }
//            }
//        }
//    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyAdapter
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

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
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