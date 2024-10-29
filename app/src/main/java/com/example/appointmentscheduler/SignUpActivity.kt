package com.example.appointmentscheduler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvAlreadyHaveAccount: TextView
    private lateinit var tvSignUpWelcome: TextView
    private lateinit var loginPageNavigation: String
    private lateinit var welcomePageNavigation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount)
        tvSignUpWelcome = findViewById(R.id.tvSignUpWelcome)

        fetchSignupData()

        btnSignup.setOnClickListener {
            // Navigate to the Welcome Activity using the retrieved navigation link
            navigateToNextPage(welcomePageNavigation)
        }

        tvAlreadyHaveAccount.setOnClickListener {
            // Navigate to the Login Activity using the retrieved navigation link
            navigateToNextPage(loginPageNavigation)
        }
    }

    private fun fetchSignupData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("signup") // Adjust this to your collection
            .document("credentials") // Replace with your document ID
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Fetch hints and button text from Firestore
                    val fullNameHint = document.getString("nameHint") ?: "Full Name"
                    val emailHint = document.getString("emailHint") ?: "Email"
                    val passwordHint = document.getString("passwordHint") ?: "Password"
                    val signupButtonText = document.getString("signupButtonText") ?: "Sign Up"
                    loginPageNavigation = document.getString("loginPageNavigation") ?: "LoginActivity" // Default navigation
                    welcomePageNavigation = document.getString("signupPageNavigation") ?: "WelcomeActivity" // Default navigation
                    val signUpWelcomeText = document.getString("signUpWelcomeText") ?: "Create Your Account"
                    val alreadyHaveAccountText = document.getString("alreadyHaveAccountText") ?: "Already have an account?"

                    // Set the hints and texts
                    etFullName.hint = fullNameHint
                    etEmail.hint = emailHint
                    etPassword.hint = passwordHint
                    btnSignup.text = signupButtonText
                    tvSignUpWelcome.text = signUpWelcomeText
                    tvAlreadyHaveAccount.text = alreadyHaveAccountText
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
            finish() // Optional: finish the SignupActivity to remove it from the back stack
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
