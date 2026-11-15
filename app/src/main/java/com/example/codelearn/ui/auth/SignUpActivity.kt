package com.example.codelearn.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.ImageButton
import android.content.Intent

class SignUpActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var etName: TextInputEditText
    private lateinit var etCampus: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var tvSelectedRole: TextView
    private lateinit var tvSignIn: TextView
    private lateinit var btnSignUp: MaterialButton

    private var selectedRole: String = "student"

    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnBack = findViewById(R.id.btnBack)
        etName = findViewById(R.id.etName)
        etCampus = findViewById(R.id.etCampus)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        tvSelectedRole = findViewById(R.id.tvSelectedRole)
        tvSignIn = findViewById(R.id.tvSignIn)
        btnSignUp = findViewById(R.id.btnSignUp)

        selectedRole = intent.getStringExtra("ROLE") ?: "student"

        tvSelectedRole.text =
            if (selectedRole == "mentor") {
                "Continue as Mentor"
            } else {
                "Continue as Student"
            }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            finish()
        }

        btnSignUp.setOnClickListener {

            val name = etName.text.toString().trim()
            val campus = etCampus.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty()) {
                etName.error = "Name is required"
                return@setOnClickListener
            }

            if (campus.isEmpty()) {
                etCampus.error = "Campus is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (password.length < 8) {
                etPassword.error = "Password minimum 8 characters"
                return@setOnClickListener
            }

            viewModel.register(
                email = email,
                password = password,
                role = selectedRole,
                name = name,
                campus = campus
            )
        }

        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {

        viewModel.isLoading.observe(this) { isLoading ->

            btnSignUp.isEnabled = !isLoading

            if (isLoading) {
                btnSignUp.text = "Creating account..."
            } else {
                btnSignUp.text = "Sign up"
            }
        }

        viewModel.authResult.observe(this) { result ->

            result.onSuccess { response ->

                Toast.makeText(
                    this,
                    response.message ?: "Account created successfully",
                    Toast.LENGTH_LONG
                ).show()

                // Nanti setelah Home Student/Mentor dibuat,
                // kita arahkan user ke halaman sesuai role.
            }

            result.onFailure { error ->

                Toast.makeText(
                    this,
                    error.message ?: "Register failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}