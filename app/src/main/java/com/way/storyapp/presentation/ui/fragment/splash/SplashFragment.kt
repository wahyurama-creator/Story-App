package com.way.storyapp.presentation.ui.fragment.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.way.storyapp.R
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launchWhenCreated {
            delay(3000)
            val action = SplashFragmentDirections.actionSplashFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}