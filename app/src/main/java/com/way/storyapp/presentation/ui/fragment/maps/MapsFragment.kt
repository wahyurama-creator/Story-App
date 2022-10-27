package com.way.storyapp.presentation.ui.fragment.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.way.storyapp.R
import com.way.storyapp.data.remote.model.story.Story
import com.way.storyapp.databinding.FragmentMapsBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.viewmodel.MapsViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import java.util.*

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private lateinit var mapsViewModel: MapsViewModel

    private lateinit var googleMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap

        googleMap.setOnMapLoadedCallback {
            mapsViewModel.readToken.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    getAllStoryLocation("Bearer $it")
                }
            }
        }

        googleMap.apply {
            setOnMapLongClickListener {
                addMarker(
                    MarkerOptions()
                        .position(it)
                        .snippet(getAddress(it))
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                )
            }

            setOnPoiClickListener {
                addMarker(
                    MarkerOptions()
                        .position(it.latLng)
                        .title(it.name)
                        .snippet(it.placeId)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                )?.showInfoWindow()
            }
        }

        getDeviceLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)

        factory = (activity as MainActivity).factory
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                getDeviceLocation()
            }
        }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addMarkerLocations(listStoryLocation: List<Story>) {
        listStoryLocation.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(getAddress(latLng))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                500
            )
        )
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        lateinit var address: Address
        lateinit var fullAddress: String
        val addresses: List<Address> =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            fullAddress = address.getAddressLine(0)
        } else {
            fullAddress = "Location not found"
        }
        return fullAddress
    }

    private fun getAllStoryLocation(token: String) {
        showLoading(true)
        mapsViewModel.getAllStoryLocation(token, setQueryParam())
        mapsViewModel.storyResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    response.data?.let {
                        addMarkerLocations(it.listStory)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ERROR", response.message.toString())
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun setQueryParam(): HashMap<String, Int> {
        val query = HashMap<String, Int>()
        query["page"] = 1
        query["size"] = 20
        query["location"] = 1
        return query
    }

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}