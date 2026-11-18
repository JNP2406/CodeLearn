package com.example.codelearn.ui.student

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.CaseStudyResponse
import com.example.codelearn.data.remote.StudentCertificateResponse
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.CaseStudyViewModel
import com.example.codelearn.viewmodel.CertificateViewModel
import com.google.android.material.button.MaterialButton

class CaseStudyActivity : AppCompatActivity() {

    private val caseStudyViewModel:
            CaseStudyViewModel by viewModels()

    private val certificateViewModel:
            CertificateViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private lateinit var btnBackArea:
            LinearLayout

    private lateinit var tvCaseStudyTitle:
            TextView

    private lateinit var tvScenario:
            TextView

    private lateinit var tvTaskDescription:
            TextView

    private lateinit var tvApprovalBadge:
            TextView

    private lateinit var tvCertificateStatus:
            TextView

    private lateinit var btnClaimCertificate:
            MaterialButton

    private var courseId =
        -1

    private var canClaimCertificate =
        false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_case_study
        )
        ActivityConfig.config(window)

        sessionManager =
            SessionManager(this)

        courseId =
            intent.getIntExtra(
                "COURSE_ID",
                -1
            )

        if (courseId == -1) {
            finish()
            return
        }

        bindViews()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {

        super.onResume()

        loadCaseStudy()
    }

    private fun bindViews() {

        btnBackArea =
            findViewById(
                R.id.btnBackArea
            )

        tvCaseStudyTitle =
            findViewById(
                R.id.tvCaseStudyTitle
            )

        tvScenario =
            findViewById(
                R.id.tvScenario
            )

        tvTaskDescription =
            findViewById(
                R.id.tvTaskDescription
            )

        tvApprovalBadge =
            findViewById(
                R.id.tvApprovalBadge
            )

        tvCertificateStatus =
            findViewById(
                R.id.tvCertificateStatus
            )

        btnClaimCertificate =
            findViewById(
                R.id.btnClaimCertificate
            )
    }

    private fun setupListeners() {

        btnBackArea.setOnClickListener {
            finish()
        }

        btnClaimCertificate.setOnClickListener {

            if (!canClaimCertificate) {
                return@setOnClickListener
            }

            claimCertificate()
        }
    }

    private fun setupObservers() {

        caseStudyViewModel.caseStudy.observe(
            this
        ) { result ->

            result.onSuccess { response ->

                showCaseStudy(
                    response
                )
            }

            result.onFailure { error ->

                Toast.makeText(
                    this,
                    error.message
                        ?: "Failed to load case study",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        certificateViewModel.claimResult.observe(
            this
        ) { result ->

            result.onSuccess { response ->

                Toast.makeText(
                    this,
                    "Certificate claimed successfully",
                    Toast.LENGTH_SHORT
                ).show()

                openCertificate(
                    response.certificate
                )
            }

            result.onFailure { error ->

                Toast.makeText(
                    this,
                    error.message
                        ?: "Failed to claim certificate",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadCaseStudy() {

        val token =
            sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            finish()
            return
        }

        caseStudyViewModel.loadCaseStudy(
            token,
            courseId
        )
    }

    private fun claimCertificate() {

        val token =
            sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            return
        }

        btnClaimCertificate.isEnabled =
            false

        btnClaimCertificate.text =
            "Claiming..."

        certificateViewModel.claimCertificate(
            token,
            courseId
        )
    }

    private fun showCaseStudy(
        response:
        CaseStudyResponse
    ) {

        tvCaseStudyTitle.text =
            response.case_study.title

        tvScenario.text =
            response.case_study.scenario

        tvTaskDescription.text =
            response.case_study.task_description

        val mentoring =
            response.mentoring

        val review =
            response.review

        canClaimCertificate =
            false

        tvApprovalBadge.visibility =
            View.GONE

        btnClaimCertificate.text =
            "Claim your certificate"

        btnClaimCertificate.isEnabled =
            false

        btnClaimCertificate.backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(
                    "#B8BDC7"
                )
            )

        if (mentoring == null) {

            tvCertificateStatus.text =
                "Find a mentor to review your case study. Your certificate unlocks after the mentor approves it."

            return
        }

        if (review == null) {

            tvCertificateStatus.text =
                "Your mentoring is active. Send your completed case study to ${mentoring.mentor_name} through chat for review."

            return
        }

        when (review.status) {

            "under_review" -> {

                tvApprovalBadge.visibility =
                    View.VISIBLE

                tvApprovalBadge.text =
                    "Under review by ${mentoring.mentor_name}"

                tvCertificateStatus.text =
                    "Your mentor is reviewing your case study. The certificate will unlock after approval."
            }

            "approved" -> {

                tvApprovalBadge.visibility =
                    View.VISIBLE

                tvApprovalBadge.text =
                    "Approved by ${mentoring.mentor_name}"

                tvCertificateStatus.text =
                    "Your mentor approved your case study. You can claim your certificate now."

                canClaimCertificate =
                    true

                btnClaimCertificate.isEnabled =
                    true

                btnClaimCertificate.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#2F6FED"
                        )
                    )
            }

            "not_approved" -> {

                tvApprovalBadge.visibility =
                    View.VISIBLE

                tvApprovalBadge.text =
                    "Not approved by ${mentoring.mentor_name}"

                tvCertificateStatus.text =
                    "Your case study was not approved. The certificate cannot be claimed."
            }
        }
    }

    private fun openCertificate(
        certificate:
        StudentCertificateResponse
    ) {

        val pdfUrl =
            certificate.pdf_url

        if (pdfUrl.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Certificate PDF not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val finalUrl =
            if (
                pdfUrl.startsWith(
                    "http"
                )
            ) {

                pdfUrl

            } else {

                "${ApiClient.BASE_URL}$pdfUrl"
            }

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        finalUrl
                    )
                )

            startActivity(
                intent
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Cannot open certificate",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}