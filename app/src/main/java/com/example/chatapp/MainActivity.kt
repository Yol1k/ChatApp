package com.example.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatapp.data.api.TokenManager
import com.example.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)

        if (!isUserLoggedIn()) {
            navController.navigate(R.id.authFragment)
        }

        navController.addOnDestinationChangedListener{_, destination, _->
            if (destination.id == R.id.chatFragment){
                binding.bottomNavView.isVisible=false
            } else if (destination.id == R.id.authFragment) {
                binding.bottomNavView.isVisible=false
            }
            else {
                binding.bottomNavView.isVisible=true
            }
        }

        val token = TokenManager.getToken(this)
        if (token == null) {
            navController.navigate(R.id.authFragment)
        } else {
            navController.navigate(R.id.action_authFragment_to_chatsFragment)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return false
    }

}


