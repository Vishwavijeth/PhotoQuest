package com.example.photoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.photoquest.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = firebaseAuth.currentUser
        Log.d(TAG, "current user: $currentUser")
        if (currentUser != null)
        {
            ReplaceFragment(HomeFragment())
            showBottomNavigation()
        }
        else{
            ReplaceFragment(LoginFragment())
            binding.bottomNavigation.visibility = View.GONE
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> ReplaceFragment(HomeFragment())
                R.id.search -> ReplaceFragment(SearchFragment())
                R.id.profile -> ReplaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    fun ReplaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    // Method to show the bottom navigation
    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
