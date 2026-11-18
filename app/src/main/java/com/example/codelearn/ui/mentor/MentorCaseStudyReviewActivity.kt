package com.example.codelearn.ui.mentor

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.MentorCaseStudyResponse
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.MentorCaseStudyViewModel

class MentorCaseStudyReviewActivity : AppCompatActivity() {

    private val viewModel: MentorCaseStudyViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private var mentoringId: Int = -1

    private var reviewId: Int = -1

    private val token: String
        get() = sessionManager.getToken() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mentor_case_study_review)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        mentoringId = intent.getIntExtra("MENTORING_ID", -1)

        if (mentoringId == -1) {
            Toast.makeText(this, "Invalid mentoring", Toast.LENGTH_SHORT).show()

            finish()
            return
        }

        setupObservers()
        setupClicks()

        viewModel.loadCaseStudy(token, mentoringId)
    }

    private fun setupObservers() {
        viewModel.data.observe(this) {
            renderData(it)
        }

        viewModel.message.observe(this) {
            if (!it.isNullOrBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                viewModel.clearMessage()
            }
        }

        viewModel.reviewFinished.observe(this) {
            if (it) {
                viewModel.loadCaseStudy(token, mentoringId)
            }
        }
    }

    private fun setupClicks() {
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnApprove).setOnClickListener {
            if (reviewId != -1) {
                viewModel.submitReview(token, reviewId, "approved")
            }
        }

        findViewById<View>(R.id.btnNotApproved).setOnClickListener {
            if (reviewId != -1) {
                viewModel.submitReview(token, reviewId, "not_approved")
            }
        }
    }

    private fun renderData(data: MentorCaseStudyResponse) {
        reviewId = data.review.id

        findViewById<TextView>(R.id.tvStudent).text =
            "${data.mentoring.student_name ?: "Student"} · ${data.mentoring.course_name}"

        findViewById<TextView>(R.id.tvCaseTitle).text = data.case_study.title

        findViewById<TextView>(R.id.tvScenario).text = data.case_study.scenario ?: "-"

        findViewById<TextView>(R.id.tvTask).text = data.case_study.task_description ?: "-"

        val status = data.review.status

        findViewById<TextView>(R.id.tvReviewStatus).text = when (status) {
            "under_review" -> "Waiting for your review"

            "approved" -> "Approved"

            "not_approved" -> "Not approved"

            else -> status
        }

        findViewById<LinearLayout>(R.id.actionContainer).visibility =
            if (status == "under_review") {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}