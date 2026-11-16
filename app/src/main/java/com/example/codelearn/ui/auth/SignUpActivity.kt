package com.example.codelearn.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.ui.mentor.MentorChatsActivity
import com.example.codelearn.ui.student.StudentHomeActivity
import com.example.codelearn.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private lateinit var etName: TextInputEditText
    private lateinit var etCampus: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var tvSelectedRole: TextView
    private lateinit var tvSignIn: TextView
    private lateinit var btnSignUp: MaterialButton
    private lateinit var btnBack: ImageButton

    private var selectedRole: String = "student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_sign_up
        )

        sessionManager =
            SessionManager(this)

        btnBack =
            findViewById(
                R.id.btnBack
            )

        etName =
            findViewById(
                R.id.etName
            )

        etCampus =
            findViewById(
                R.id.etCampus
            )

        etEmail =
            findViewById(
                R.id.etEmail
            )

        etPassword =
            findViewById(
                R.id.etPassword
            )

        tvSelectedRole =
            findViewById(
                R.id.tvSelectedRole
            )

        tvSignIn =
            findViewById(
                R.id.tvSignIn
            )

        btnSignUp =
            findViewById(
                R.id.btnSignUp
            )

        selectedRole =
            intent.getStringExtra(
                "ROLE"
            ) ?: "student"

        tvSelectedRole.text =
            if (
                selectedRole == "mentor"
            ) {
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
            val name =
                etName.text
                    .toString()
                    .trim()

            val campus =
                etCampus.text
                    .toString()
                    .trim()

            val email =
                etEmail.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()

            if (name.isEmpty()) {
                etName.error =
                    "Name is required"

                return@setOnClickListener
            }

            if (campus.isEmpty()) {
                etCampus.error =
                    "Campus is required"

                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error =
                    "Email is required"

                return@setOnClickListener
            }

            if (password.length < 8) {
                etPassword.error =
                    "Password minimum 8 characters"

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
            val intent =
                Intent(
                    this,
                    SignInActivity::class.java
                )

            startActivity(intent)
        }
    }

    private fun setupObservers() {

        viewModel.isLoading.observe(this) { isLoading ->
            btnSignUp.isEnabled =
                !isLoading

            btnSignUp.text =
                if (isLoading) {
                    "Creating account..."
                } else {
                    "Sign up"
                }
        }

        viewModel.authResult.observe(this) { result ->

            result.onSuccess { response ->

                val token =
                    response.token

                val user =
                    response.user

                if (
                    token == null ||
                    user == null
                ) {
                    Toast.makeText(
                        this,
                        "Invalid registration response",
                        Toast.LENGTH_LONG
                    ).show()

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

                Toast.makeText(
                    this,
                    "Welcome ${user.name}",
                    Toast.LENGTH_SHORT
                ).show()

                when (
                    user.role.lowercase()
                ) {
                    "student" -> {
                        openStudentHome()
                    }

                    "mentor" -> {
                        openMentorHome()
                    }

                    else -> {
                        sessionManager.logout()

                        Toast.makeText(
                            this,
                            "Unknown user role",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            result.onFailure { error ->
                Toast.makeText(
                    this,
                    error.message
                        ?: "Register failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openStudentHome() {
        val intent =
            Intent(
                this,
                StudentHomeActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun openMentorHome() {
        val intent =
            Intent(
                this,
                MentorChatsActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}