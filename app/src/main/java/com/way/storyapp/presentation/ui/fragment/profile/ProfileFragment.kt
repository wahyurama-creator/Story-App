package com.way.storyapp.presentation.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.way.storyapp.R
import com.way.storyapp.databinding.FragmentProfileBinding
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.viewmodel.AuthViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        factory = (activity as MainActivity).factory
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            getAccount()
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            performBackAction(toSignIn = true, toList = false)
        }

        handleBackStack()

        changeLanguage()
    }

    private fun changeLanguage() {
        binding.ivLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.tvLanguage.text = Locale.getDefault().country
    }

    private fun performBackAction(toSignIn: Boolean = false, toList: Boolean = false) {
        when {
            toSignIn && !toList -> {
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            }
            toList && !toSignIn -> {
                findNavController().navigate(R.id.action_profileFragment_to_listStoryFragment)
            }
        }
    }

    private fun handleBackStack() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackAction(toSignIn = false, toList = true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun getAccount() {
        authViewModel.readAccount.observe(viewLifecycleOwner) {
            if (it.stateLogin) {
                binding.apply {
                    tvUsername.text = it.name
                    tvToken.text = it.token
                }
            }
        }
    }
}