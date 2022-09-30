package com.way.storyapp.presentation.ui.fragment.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.way.storyapp.R
import com.way.storyapp.databinding.FragmentAddStoryBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.fragment.add.camera.CameraData
import com.way.storyapp.presentation.ui.utils.*
import com.way.storyapp.presentation.ui.viewmodel.AddStoryViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var factory: ViewModelFactory

    private val argsCamera by navArgs<AddStoryFragmentArgs>()
    private lateinit var cameraData: CameraData
    private var imgFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(layoutInflater, container, false)

        factory = (activity as MainActivity).factory
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (argsCamera.argsCameraData != null) {
            cameraData = argsCamera.argsCameraData!!
            imgFile = cameraData.file
            Log.d("ImageFileCamera", imgFile.toString())
            val isBackCamera = cameraData.isBackCamera
            val result = rotateBitmap(BitmapFactory.decodeFile(imgFile!!.path), isBackCamera)
            binding.ivStory.load(result)
        }

        handleBackStack()
        handleEditText()

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }

        binding.btnUpload.setOnClickListener {
            it.hideKeyboard()
            lifecycleScope.launch {
                addStoryViewModel.readToken.observe(viewLifecycleOwner) {
                    validateData(auth = "Bearer $it")
                }
            }
        }
    }

    private fun validateData(auth: String) {
        val file = reduceFileImage(imgFile as File)
        Log.d("ImageFileValidate", imgFile.toString())
        val description = binding.etDescription.text.toString()
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )
        postStory(
            auth, imageMultipart, addStoryViewModel.setMapBody(
                description, 0.0, 0.0
            )
        )
    }

    private fun postStory(
        auth: String, file: MultipartBody.Part, parts: HashMap<String, RequestBody>
    ) {
        showLoading(true)
        addStoryViewModel.postStory(auth, file, parts)
        addStoryViewModel.postStory.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(context, getString(R.string.story_success), Toast.LENGTH_SHORT)
                        .show()
                    performBackAction()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(), response.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                    binding.btnUpload.isEnabled = false
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a image")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            imgFile = myFile
            Log.d("ImageFileGallery", imgFile.toString())
            binding.ivStory.setImageURI(selectedImg)
        }
    }

    private fun startCameraX() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION_CAMERA
            )
        }
        val action = AddStoryFragmentDirections.actionAddStoryFragmentToCameraFragment()
        findNavController().navigate(action)
    }

    private fun performBackAction() {
        findNavController().navigate(R.id.action_addStoryFragment_to_listStoryFragment)
    }

    private fun handleBackStack() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackAction()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun handleEditText() {
        binding.apply {
            etDescription.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
        }
    }

    private fun handleButton() {
        val description = binding.etDescription.text.toString()
        binding.btnUpload.isEnabled = description.isNotEmpty() && imgFile != null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    context, getString(R.string.not_have_permission), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
        )
        private const val REQUEST_CODE_PERMISSION_CAMERA = 10
    }
}