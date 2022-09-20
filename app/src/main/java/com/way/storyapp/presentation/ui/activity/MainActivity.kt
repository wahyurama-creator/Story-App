package com.way.storyapp.presentation.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.way.storyapp.R
import com.way.storyapp.databinding.ActivityMainBinding
import com.way.storyapp.presentation.ui.fragment.list.adapter.StoryAdapter
import com.way.storyapp.presentation.ui.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Visible
                R.id.listStoryFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                R.id.profileFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                R.id.addStoryFragment -> binding.bottomNavigationView.visibility = View.VISIBLE

                // Invisible
                R.id.splashFragment -> binding.bottomNavigationView.visibility = View.INVISIBLE
                R.id.signInFragment -> binding.bottomNavigationView.visibility = View.INVISIBLE
                R.id.signUpFragment -> binding.bottomNavigationView.visibility = View.INVISIBLE
                R.id.cameraFragment -> binding.bottomNavigationView.visibility = View.INVISIBLE
                R.id.detailFragment -> binding.bottomNavigationView.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}