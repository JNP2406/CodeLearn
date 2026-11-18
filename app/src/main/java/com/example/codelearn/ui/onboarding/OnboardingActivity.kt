package com.example.codelearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.ui.auth.SignUpActivity
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.OnboardingViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class OnboardingActivity : AppCompatActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    private lateinit var imgOnboarding: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPageIndicator: TextView

    private lateinit var layoutBenefits: LinearLayout
    private lateinit var layoutRoleSelection: LinearLayout

    private lateinit var cardStudent: MaterialCardView
    private lateinit var cardMentor: MaterialCardView

    private lateinit var btnBack: MaterialButton
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        ActivityConfig.config(window)

        imgOnboarding = findViewById(R.id.imgOnboarding)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvPageIndicator = findViewById(R.id.tvPageIndicator)

        layoutBenefits = findViewById(R.id.layoutBenefits)
        layoutRoleSelection = findViewById(R.id.layoutRoleSelection)

        cardStudent = findViewById(R.id.cardStudent)
        cardMentor = findViewById(R.id.cardMentor)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        btnNext.setOnClickListener {

            when (viewModel.currentPage.value ?: 0) {

                0 -> {
                    viewModel.nextPage()
                }

                1 -> {
                    if (viewModel.selectedRole.value == null) {
                        Toast.makeText(
                            this, "Pilih Student atau Mentor terlebih dahulu.", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.nextPage()
                    }
                }

                2 -> {
                    val role = viewModel.selectedRole.value

                    if (role == null) {
                        Toast.makeText(this, "Role belum dipilih.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, SignUpActivity::class.java)

                    intent.putExtra("ROLE", role)

                    startActivity(intent)
                }
            }
        }

        btnBack.setOnClickListener {
            viewModel.previousPage()
        }

        cardStudent.setOnClickListener {
            viewModel.selectRole("student")
        }

        cardMentor.setOnClickListener {
            viewModel.selectRole("mentor")
        }
    }

    private fun setupObservers() {

        viewModel.currentPage.observe(this) { page ->
            showPage(page)
        }

        viewModel.selectedRole.observe(this) { role ->
            updateRoleSelection(role)
        }
    }

    private fun showPage(page: Int) {

        when (page) {

            0 -> {
                imgOnboarding.setImageResource(R.drawable.onboarding_1)

                tvTitle.text = "Belajar coding jadi lebih mudah"

                tvDescription.text = "Dari nol sampai paham, satu sesi dalam satu waktu."

                layoutBenefits.visibility = View.VISIBLE
                layoutRoleSelection.visibility = View.GONE

                btnBack.visibility = View.GONE
                btnNext.text = "Lanjut"

                tvPageIndicator.text = "● ○ ○"
            }

            1 -> {
                imgOnboarding.setImageResource(R.drawable.onboarding_2)

                tvTitle.text = "Kamu mau jadi apa di CodeLearn?"

                tvDescription.text = "Pilih satu peran. Kamu bisa membaca detailnya di bawah."

                layoutBenefits.visibility = View.GONE
                layoutRoleSelection.visibility = View.VISIBLE

                btnBack.visibility = View.VISIBLE
                btnNext.text = "Lanjut"

                tvPageIndicator.text = "○ ● ○"
            }

            2 -> {
                imgOnboarding.setImageResource(R.drawable.onboarding_3)

                tvTitle.text = "Ayo mulai perjalananmu!"

                tvDescription.text =
                    "Buat akunmu sekarang, lalu mulai belajar atau berbagi pengetahuan bersama CodeLearn."

                layoutBenefits.visibility = View.GONE
                layoutRoleSelection.visibility = View.GONE

                btnBack.visibility = View.VISIBLE
                btnNext.text = "Sign up"

                tvPageIndicator.text = "○ ○ ●"
            }
        }
    }

    private fun updateRoleSelection(role: String?) {

        val blue = getColor(R.color.blue)
        val gray = getColor(android.R.color.darker_gray)

        when (role) {

            "student" -> {
                cardStudent.strokeColor = blue
                cardStudent.strokeWidth = 3

                cardMentor.strokeColor = gray
                cardMentor.strokeWidth = 1
            }

            "mentor" -> {
                cardStudent.strokeColor = gray
                cardStudent.strokeWidth = 1

                cardMentor.strokeColor = blue
                cardMentor.strokeWidth = 3
            }

            else -> {
                cardStudent.strokeColor = gray
                cardStudent.strokeWidth = 1

                cardMentor.strokeColor = gray
                cardMentor.strokeWidth = 1
            }
        }
    }
}