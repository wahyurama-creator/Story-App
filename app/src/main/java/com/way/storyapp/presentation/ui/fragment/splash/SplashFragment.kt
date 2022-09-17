package com.way.storyapp.presentation.ui.fragment.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.way.storyapp.R
import com.way.storyapp.presentation.ui.activity.MainActivity
import com.way.storyapp.presentation.ui.viewmodel.AuthViewModel
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        factory = (activity as MainActivity).factory
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            delay(3000)

            authViewModel.readAccount.observe(viewLifecycleOwner) {
                if (it.stateLogin) {
                    val action = SplashFragmentDirections.actionSplashFragmentToListStoryFragment()
                    findNavController().navigate(action)
                } else {
                    val action = SplashFragmentDirections.actionSplashFragmentToSignInFragment()
                    findNavController().navigate(action)
                }
            }
        }

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}