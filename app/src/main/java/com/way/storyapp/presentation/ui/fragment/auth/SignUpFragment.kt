package com.way.storyapp.presentation.ui.fragment.auth

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.way.storyapp.data.remote.model.auth.UserRegisterData
import com.way.storyapp.databinding.FragmentSignUpBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isValidEmail
import com.way.storyapp.presentation.ui.viewmodel.AuthViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

//    private lateinit var cameraData: CameraData
//    private var isPictureClicked = false

    private lateinit var factory: ViewModelFactory
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)

        factory = (activity as MainActivity).factory
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        if (args.argsCamera != null) {
//            cameraData = args.argsCamera!!
//            isPictureClicked = true
//            showImageData()
//        }

//        binding.ivUser.setOnClickListener { startCameraX() }

        binding.ivBack.setOnClickListener { performBackAction() }

        handleEditText()

        binding.button.setOnClickListener {

            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val userRegisterData = UserRegisterData(name, email, password)
            postRegister(userRegisterData)

        }
    }

    private fun postRegister(userRegisterData: UserRegisterData) {
        showLoading(true)
        authViewModel.postRegister(userRegisterData)
        authViewModel.postRegister.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                    performBackAction()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

//    private fun showImageData() {
//        val imgFile = cameraData.file
//        val isBackCamera = cameraData.isBackCamera
//        val result = rotateBitmap(BitmapFactory.decodeFile(imgFile.path), isBackCamera)
//        binding.ivUser.load(result) {
//            transformations(CircleCropTransformation())
//        }
//    }

//    private fun startCameraX() {
//        if (!allPermissionGranted()) {
//            ActivityCompat.requestPermissions(
//                requireActivity(), REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION
//            )
//        }
//        val action = SignUpFragmentDirections.actionSignUpFragmentToCameraFragment()
//        findNavController().navigate(action)
//    }


    private fun performBackAction() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)
    }

//    @Suppress("DEPRECATION")
//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSION) {
//            if (!allPermissionGranted()) {
//                Toast.makeText(
//                    context,
//                    getString(R.string.not_have_permission),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
//        ContextCompat.checkSelfPermission(
//            requireContext(),
//            it
//        ) == PackageManager.PERMISSION_GRANTED
//    }

    private fun handleEditText() {
        binding.apply {
            etName.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
            etEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
            etPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
        }
    }

    private fun handleButton() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        binding.button.isEnabled =
            name.isNotEmpty() && email.isValidEmail() && email.isNotEmpty() && password.length > 6 && password.isNotEmpty()
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