package com.way.storyapp.presentation.ui.fragment.auth

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


    private fun performBackAction() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)
    }

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}