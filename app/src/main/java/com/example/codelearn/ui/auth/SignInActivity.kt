package com.example.codelearn.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.ui.mentor.MentorChatsActivity
import com.example.codelearn.ui.student.StudentHomeActivity
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var sessionManager: SessionManager
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var tvSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvSignUp = findViewById(R.id.tvSignUp)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()

            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                etEmail.error = "Email is required"

                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password is required"

                return@setOnClickListener
            }

            viewModel.login(email = email, password = password)
        }

        tvSignUp.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            btnSignIn.isEnabled = !isLoading

            btnSignIn.text = if (isLoading) {
                "Signing in..."
            } else {
                "Sign in"
            }
        }

        viewModel.authResult.observe(this) { result ->
            result.onSuccess { response ->
                val token = response.token

                val user = response.user

                if (token == null || user == null) {
                    Toast.makeText(this, "Invalid login response", Toast.LENGTH_LONG).show()

                    return@onSuccess
                }

                sessionManager.saveLoginSession(
                    token = token,
                    userId = user.id,
                    email = user.email,
                    role = user.role,
                    name = user.name,
                    campus = user.campus,
                    isDemo = user.is_demo
                )

                Toast.makeText(this, "Welcome ${user.name}", Toast.LENGTH_SHORT).show()

                when (user.role.lowercase()) {
                    "student" -> {
                        openStudentHome()
                    }

                    "mentor" -> {
                        openMentorHome()
                    }

                    else -> {
                        sessionManager.logout()

                        Toast.makeText(this, "Unknown user role", Toast.LENGTH_LONG).show()
                    }
                }
            }

            result.onFailure { error ->
                Toast.makeText(this, error.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openStudentHome() {
        val intent = Intent(this, StudentHomeActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun openMentorHome() {
        val intent = Intent(this, MentorChatsActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}