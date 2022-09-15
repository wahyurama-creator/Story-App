package com.way.storyapp.presentation.ui.fragment.auth

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.way.storyapp.R
import com.way.storyapp.databinding.FragmentSignUpBinding
import com.way.storyapp.presentation.ui.fragment.auth.camera.CameraData
import com.way.storyapp.presentation.ui.utils.rotateBitmap

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<SignUpFragmentArgs>()
    private lateinit var cameraData: CameraData
    private var isPictureClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        performBackAction()

        if (args.argsCamera != null) {
            cameraData = args.argsCamera!!
            isPictureClicked = true
            showImageData()
        }

        binding.ivUser.setOnClickListener { startCameraX() }
    }

    private fun showImageData() {
        val imgFile = cameraData.file
        val isBackCamera = cameraData.isBackCamera
        val result = rotateBitmap(BitmapFactory.decodeFile(imgFile.path), isBackCamera)
        binding.ivUser.load(result) {
            transformations(CircleCropTransformation())
        }
    }

    private fun startCameraX() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION
            )
        }
        val action = SignUpFragmentDirections.actionSignUpFragmentToCameraFragment()
        findNavController().navigate(action)
    }


    private fun performBackAction() {
        binding.ivBack.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    context,
                    getString(R.string.not_have_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}