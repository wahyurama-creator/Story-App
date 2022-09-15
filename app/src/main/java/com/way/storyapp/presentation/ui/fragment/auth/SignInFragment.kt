package com.way.storyapp.presentation.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.way.storyapp.databinding.FragmentSignInBinding
import com.way.storyapp.presentation.ui.utils.isValidEmail

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleEditText()

        binding.button.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToListStoryFragment()
            findNavController().navigate(action)
        }

        binding.tvSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}