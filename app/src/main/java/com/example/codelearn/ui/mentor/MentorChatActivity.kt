package com.example.codelearn.ui.mentor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.viewmodel.MentoringChatViewModel

class MentorChatActivity : AppCompatActivity() {

    private val viewModel:
            MentoringChatViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private var mentoringId: Int = -1
    private var includeFinalProject: Int = 0

    private val token: String
        get() =
            sessionManager.getToken() ?: ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_mentor_chat
        )

        sessionManager =
            SessionManager(this)

        readIntent()
        setupHeader()
        setupObservers()
        setupClicks()

        if (mentoringId != -1) {
            viewModel.loadStatus(
                token,
                mentoringId
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            mentoringId != -1
        ) {
            viewModel.loadStatus(
                token,
                mentoringId
            )
        }
    }

    private fun readIntent() {
        mentoringId =
            intent.getIntExtra(
                "MENTORING_ID",
                -1
            )

        includeFinalProject =
            intent.getIntExtra(
                "INCLUDE_FINAL_PROJECT",
                0
            )
    }

    private fun setupHeader() {
        val name =
            intent.getStringExtra(
                "PARTNER_NAME"
            ) ?: "Student"

        val image =
            intent.getStringExtra(
                "PROFILE_IMAGE"
            )

        val course =
            intent.getStringExtra(
                "COURSE_NAME"
            ) ?: "Course"

        val startSession =
            intent.getIntExtra(
                "START_SESSION",
                1
            )

        val endSession =
            intent.getIntExtra(
                "END_SESSION",
                startSession
            )

        findViewById<TextView>(
            R.id.tvPartnerName
        ).text =
            name

        AvatarHelper.show(
            imageView =
                findViewById<ImageView>(
                    R.id.ivAvatar
                ),
            initialView =
                findViewById<TextView>(
                    R.id.tvAvatarInitial
                ),
            imagePath =
                image,
            name =
                name
        )

        findViewById<TextView>(
            R.id.tvMentoringInfo
        ).text =
            if (
                includeFinalProject == 1
            ) {
                "$course · Session $startSession to Final project"
            } else {
                "$course · Sessions $startSession-$endSession"
            }

        findViewById<View>(
            R.id.projectBanner
        ).visibility =
            if (
                includeFinalProject == 1
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun setupObservers() {
        viewModel.status.observe(this) {
            setChatAvailability(
                it.can_chat
            )
        }

        viewModel.message.observe(this) {
            if (!it.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.clearMessage()
            }
        }
    }

    private fun setupClicks() {
        findViewById<View>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }

        findViewById<View>(
            R.id.projectBanner
        ).setOnClickListener {
            if (
                includeFinalProject == 1 &&
                mentoringId != -1
            ) {
                val intent =
                    Intent(
                        this,
                        MentorCaseStudyReviewActivity::class.java
                    )

                intent.putExtra(
                    "MENTORING_ID",
                    mentoringId
                )

                startActivity(intent)
            }
        }

        findViewById<View>(
            R.id.btnAttachment
        ).setOnClickListener {
            Toast.makeText(
                this,
                "Attachment will be connected to chat backend",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(
            R.id.btnSend
        ).setOnClickListener {
            val text =
                findViewById<EditText>(
                    R.id.etMessage
                ).text
                    .toString()
                    .trim()

            if (text.isBlank()) {
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Message API will be connected by chat module",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setChatAvailability(
        canChat: Boolean
    ) {
        findViewById<View>(
            R.id.inputArea
        ).visibility =
            if (canChat) {
                View.VISIBLE
            } else {
                View.GONE
            }

        findViewById<View>(
            R.id.tvReadOnly
        ).visibility =
            if (canChat) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }
}