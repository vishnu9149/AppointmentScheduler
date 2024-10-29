package com.example.appointmentscheduler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {

    private lateinit var imgWelcome: ImageView
    private lateinit var tvWelcome: TextView
    private lateinit var btnStart: Button
    private lateinit var navigationLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        imgWelcome = findViewById(R.id.imgWelcome)
        tvWelcome = findViewById(R.id.tvWelcome)
        btnStart = findViewById(R.id.btnStart)

        fetchWelcomeData()

        btnStart.setOnClickListener {
            // Navigate to the activity retrieved from Firestore
            navigateToNextPage(navigationLink)
        }
    }

    private fun fetchWelcomeData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("welcome")
            .document("content")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val welcomeText = document.getString("welcomeText") ?: "Welcome to Appointment Scheduler"
                    val imageUrl = document.getString("imageUrl") ?: ""
                    navigationLink = document.getString("nextPageNavigation") ?: ""
                    val buttonText = document.getString("buttonText") ?: "Get Started"

                    tvWelcome.text = welcomeText
                    Glide.with(this).load(imageUrl).into(imgWelcome)
                    btnStart.text = buttonText // Set the button text
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
            finish() // Optional: finish the WelcomeActivity to remove it from the back stack
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
