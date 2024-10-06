package com.example.photoquest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_login, container, false)

        val continueLogin: TextView = root.findViewById(R.id.continueLogin)
        val emailInput: EditText = root.findViewById(R.id.emailLogin)
        val passwordInput: EditText = root.findViewById(R.id.passwordlogin)

        continueLogin.setOnClickListener {
            val emailText = emailInput.text.toString()
            val passwordText = passwordInput.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User exists, navigate to HomeFragment
                            val homeFragment = HomeFragment()
                            (activity as MainActivity).ReplaceFragment(homeFragment)
                            (activity as MainActivity).showBottomNavigation()
                        } else {
                            // User does not exist, show invalid account toast
                            Toast.makeText(requireContext(), "Invalid account", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        val signup: TextView = root.findViewById(R.id.signupText)
        signup.setOnClickListener {
            SignupListener()
        }

        return root
    }

    private fun SignupListener()
    {
        val registerFragment = RegisterFragment()
        (activity as MainActivity).ReplaceFragment(registerFragment)
    }

    companion object {
        const val TAG = "Login"
    }
}
