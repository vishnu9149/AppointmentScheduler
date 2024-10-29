package com.example.appointmentscheduler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingImage: ImageView
    private lateinit var onboardingText: TextView
    private lateinit var createdByText: TextView
    private lateinit var getStartedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        onboardingImage = findViewById(R.id.onboardingImage)
        onboardingText = findViewById(R.id.onboardingText)
        createdByText = findViewById(R.id.createdByText)
        getStartedButton = findViewById(R.id.getStartedButton)

        fetchOnboardingData()
    }

    private fun fetchOnboardingData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("1").document("1")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageUrl = document.getString("image_url")
                    val mainText = document.getString("main_text")
                    val createdBy = document.getString("created_by_text")
                    val buttonText = document.getString("button_text")
                    val nextActivity = document.getString("nextActivity")

                    // Load image using Glide
                    Glide.with(this)
                        .load(imageUrl)
                        .into(onboardingImage)

                    onboardingText.text = mainText
                    createdByText.text = createdBy
                    getStartedButton.text = buttonText

                    getStartedButton.setOnClickListener {
                        // Navigate to the next activity
                        val intent = Intent(this, Class.forName("com.example.appointmentscheduler.$nextActivity"))
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
                exception.printStackTrace()
            }
    }
}
