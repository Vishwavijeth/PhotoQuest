package com.example.photoquest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_register, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val continueRegister: TextView = root.findViewById(R.id.continueRegister)
        val signin: TextView = root.findViewById(R.id.signinText)

        val emailInput: EditText = root.findViewById(R.id.emailRegister)
        val passwordInput: EditText = root.findViewById(R.id.password_register)
        val retypeInput: EditText = root.findViewById(R.id.retypepassword)
        val usernameInput: EditText = root.findViewById(R.id.username)

        signin.setOnClickListener {
            SignInListener()
        }

        continueRegister.setOnClickListener {
            val emailText = emailInput.text.toString()
            val passText = passwordInput.text.toString()
            val rePassText = retypeInput.text.toString()
            val usernameText = usernameInput.text.toString()


            if (emailText.isNotEmpty() && passText.isNotEmpty() && rePassText.isNotEmpty())
            {
                if (passText == rePassText)
                {
                    firebaseAuth.createUserWithEmailAndPassword(emailText, passText).addOnCompleteListener {
                        if (it.isSuccessful)
                        {
                            val userId = firebaseAuth.currentUser?.uid
                            Log.d(TAG, "userid: $userId")

                            val userMap = hashMapOf(
                                "username" to usernameText,
                                "email" to emailText
                            )

                            if (userId != null)
                            {
                                firestore.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()

                                        val loginFragment = LoginFragment()
                                        (activity as MainActivity).ReplaceFragment(loginFragment)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, it.message.toString())
                                    }
                            }
                        }
                        else
                        {
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            Log.d(TAG, it.exception.toString())
                        }
                    }
                }
                else
                {
                    Toast.makeText(context, "Password not matched", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }

        }






        return root
    }

    private fun SignInListener()
    {
        val loginFragment = LoginFragment()
        (activity as MainActivity).ReplaceFragment(loginFragment)
    }

    companion object {
        const val TAG = "RegisterFragment"
    }
}