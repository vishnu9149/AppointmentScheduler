package com.example.appointmentscheduler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnBackToMain: Button // New button
    private lateinit var logoutNavigation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profileImage)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnBackToMain = findViewById(R.id.btnBackToMain) // Initialize the new button

        fetchProfileData()

        btnLogout.setOnClickListener {
            // Navigate to the logout page retrieved from Firestore
            navigateToNextPage(logoutNavigation)
        }

        btnBackToMain.setOnClickListener {
            // Go back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish the ProfileActivity to remove it from the back stack
        }
    }

    private fun fetchProfileData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users") // Adjust this to your Firestore collection
            .document("userProfile") // Replace with your document ID
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Fetch the name, email, image URL, and logout navigation from Firestore
                    val name = document.getString("name") ?: "John Doe"
                    val email = document.getString("email") ?: "john.doe@example.com"
                    val imageUrl = document.getString("imageUrl") ?: "" // URL of the image
                    logoutNavigation = document.getString("logoutPageNavigation") ?: "LoginActivity" // Default navigation
                    val logoutButtonText = document.getString("logoutButtonText") ?: "Logout" // Default button text

                    // Set the fetched data to the views
                    tvName.text = name
                    tvEmail.text = email
                    Glide.with(this).load(imageUrl).into(profileImage) // Load the image using Glide
                    btnLogout.text = logoutButtonText // Set the button text
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun navigateToNextPage(pageName: String) {
        try {
            val intent = Intent(this, Class.forName("com.example.appointmentscheduler.$pageName"))
            startActivity(intent)
            finish() // Optional: finish the ProfileActivity to remove it from the back stack
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
