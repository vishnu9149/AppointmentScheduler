package com.example.appointmentscheduler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var tvWelcomeBack: TextView // Added for welcome back message
    private lateinit var loginPageNavigation: String
    private lateinit var signupPageNavigation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.btnSignup)
        tvWelcomeBack = findViewById(R.id.tvWelcomeBack) // Initialize the welcome back TextView

        fetchLoginData()

        btnLogin.setOnClickListener {
            // Handle login logic here if needed
            navigateToNextPage(loginPageNavigation)
        }

        btnSignup.setOnClickListener {
            navigateToNextPage(signupPageNavigation)
        }
    }

    private fun fetchLoginData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("login") // Adjust this to your collection
            .document("credentials") // Replace with your document ID
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val emailHint = document.getString("emailHint")
                    val passwordHint = document.getString("passwordHint")
                    val loginButtonText = document.getString("loginButtonText")
                    val signupButtonText = document.getString("signupButtonText")
                    val welcomeBackMessage = document.getString("welcomeBackText")
                    loginPageNavigation = document.getString("loginPageNavigation") ?: "DefaultActivity"
                    signupPageNavigation = document.getString("signupPageNavigation") ?: "SignupActivity"

                    etEmail.hint = emailHint
                    etPassword.hint = passwordHint
                    btnLogin.text = loginButtonText
                    btnSignup.text = signupButtonText

                    // Set the welcome message and make it visible
                    tvWelcomeBack.text = welcomeBackMessage
                    tvWelcomeBack.visibility = TextView.VISIBLE
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
            finish() // Optional: finish the LoginActivity to remove it from the back stack
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
