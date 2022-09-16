package com.way.storyapp.presentation.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.way.storyapp.data.remote.model.auth.UserRegisterData
import com.way.storyapp.databinding.FragmentSignInBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isValidEmail
import com.way.storyapp.presentation.ui.viewmodel.AuthViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)

        factory = (activity as MainActivity).factory
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleEditText()

        binding.button.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val userRegisterData = UserRegisterData(null, email, password)
                postLogin(userRegisterData)
            }
        }

        binding.tvSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun postLogin(userRegisterData: UserRegisterData) {
        showLoading(true)
        authViewModel.postLogin(userRegisterData)
        authViewModel.postLogin.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                    val action = SignInFragmentDirections.actionSignInFragmentToListStoryFragment()
                    findNavController().navigate(action)
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

    private fun handleEditText() {
        binding.apply {
            etEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
            etPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                handleButton()
            })
        }
    }

    private fun handleButton() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        binding.button.isEnabled =
            email.isValidEmail() && email.isNotEmpty() && password.length > 6 && password.isNotEmpty()
    }

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}