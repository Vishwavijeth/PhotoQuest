package com.example.photoquest

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class ProfileFragment : Fragment() {

    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var displayUsername: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val favoriteList = mutableListOf<Favorite>()
    private lateinit var gridLayoutManager: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_profile, container, false)

        val logoutButton: ImageButton = root.findViewById(R.id.logoutButton)
        val selectFormat: TextView = root.findViewById(R.id.compact)
        displayUsername = root.findViewById(R.id.displayusername)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        favoriteRecyclerView = root.findViewById(R.id.favoritesRecyclerView)
        favoriteRecyclerView.layoutManager = GridLayoutManager(context, 2)
        gridLayoutManager = GridLayoutManager(context, 2)
        favoriteAdapter = FavoriteAdapter(favoriteList)
        favoriteRecyclerView.adapter = favoriteAdapter

        val userid = firebaseAuth.currentUser?.uid

        if (userid != null)
        {
            DisplayUsername(userid)
        } else {
            displayUsername.text = ""
        }


        selectFormat.setOnClickListener {
            ShowPopupMenu(it)
        }

        logoutButton.setOnClickListener{
            LogoutListener()
        }

        LoadFavoriteImages()

        return root
    }

    private fun ShowPopupMenu(view: View)
    {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add("Compact")
        popupMenu.menu.add("Wide")

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            (view as TextView).text = menuItem.title
            UpdateLayoutBasedOnFormat(menuItem.title.toString())
            true
        }

        popupMenu.show()
    }

    private fun UpdateLayoutBasedOnFormat(selectedFormat: String)
    {
        when (selectedFormat) {
            "Compact" -> {
                gridLayoutManager.spanCount = 2
                favoriteAdapter.currentFormat = "Compact"
            }
            "Wide" -> {
                gridLayoutManager.spanCount = 1
                favoriteAdapter.currentFormat = "Wide"
            }
        }
        favoriteRecyclerView.layoutManager = gridLayoutManager
        favoriteAdapter.notifyDataSetChanged()
    }


    private fun DisplayUsername(userid: String)
    {
        firestore.collection("users").document(userid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists())
                {
                    val username = document.getString("username") ?: ""
                    displayUsername.text = "$username's  Favorites"
                }
                else{
                    Log.d(TAG, "no username")
                    displayUsername.text = ""
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting user username: ${it.message}")
                displayUsername.text = ""
            }

    }

    private fun LoadFavoriteImages()
    {
        val userid = FirebaseAuth.getInstance().currentUser?.uid

        if (userid != null)
        {
            val db = FirebaseFirestore.getInstance()
            db.collection("Favorite")
                .whereEqualTo("userId", userid)
                .get()
                .addOnSuccessListener {
                    favoriteList.clear()
                    for (document in it)
                    {
                        val favorite = document.toObject(Favorite::class.java)
                        favoriteList.add(favorite)
                    }
                    favoriteAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load favorites: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun LogoutListener()
    {
        val dialogView = layoutInflater.inflate(R.layout.logout_dialog, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()

        val cancel_btn: TextView = dialogView.findViewById(R.id.cancelBtn)
        val yes_btn: TextView = dialogView.findViewById(R.id.yesBtn)

        cancel_btn.setOnClickListener {
            dialog.dismiss() // Close the dialog when Cancel is clicked
        }

        yes_btn.setOnClickListener {
            PerformLogout() // Call logout logic
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun PerformLogout()
    {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        val loginFragment = LoginFragment()
        (activity as MainActivity).ReplaceFragment(loginFragment)
        (activity as MainActivity).findViewById<View>(R.id.bottomNavigation).visibility = View.GONE

        // Optionally, you can show a toast to confirm logout
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}