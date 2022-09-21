package com.way.storyapp.presentation.ui.fragment.detail

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.way.storyapp.R
import com.way.storyapp.databinding.FragmentDetailBinding
import java.util.*

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val storyArgs by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnBackPressed()
        binding.ivBack.setOnClickListener { performBackAction() }

        applyData()
    }

    private fun performBackAction() {
        val action = DetailFragmentDirections.actionDetailFragmentToListStoryFragment()
        findNavController().navigate(action)
    }

    private fun applyData() {
        val story = storyArgs.storyArgs
        binding.apply {
            ivStory.load(story.photoUrl) {
                crossfade(true)
                crossfade(500)
            }
            tvTitle.text = story.name
            tvDescription.text = story.description

            //2022-09-19T01:58:17.674Z
            tvDate.text = story.createdAt.substringBefore("T")
            tvTime.text = story.createdAt.substringAfter("T").substringBefore(".")
            tvLocation.text = getAddress(LatLng(story.lat, story.lon)) ?: "Unavailable"
            tvLocationDetail.text = getString(R.string.detail_location, story.lat, story.lon) ?: "Unavailable"
            tvDescription.text = story.description
        }
    }

    private fun getAddress(latLng: LatLng?): String {
        if (latLng != null) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address: Address?
            val addressText: String

            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            } else {
                addressText = "its not appear"
            }
            return addressText
        } else {
            return "Location is not available"
        }
    }

    private fun handleOnBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackAction()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}