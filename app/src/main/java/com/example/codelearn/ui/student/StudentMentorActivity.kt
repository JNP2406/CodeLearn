package com.example.codelearn.ui.student

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.StudentMentoringItem
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.util.NotificationBadgeHelper
import com.example.codelearn.viewmodel.StudentMentorViewModel

class StudentMentorActivity : AppCompatActivity() {

    private val viewModel:
            StudentMentorViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private val token: String
        get() = sessionManager.getToken() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_student_mentor
        )

        sessionManager =
            SessionManager(this)

        setupObservers()
        setupClicks()
        setupNavigation()
        setupNotification()

        viewModel.loadMentorings(token)
    }

    override fun onResume() {
        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            token.isNotBlank()
        ) {
            viewModel.loadMentorings(token)

            NotificationBadgeHelper.refresh(
                this,
                sessionManager,
                R.id.tvNotificationBadge
            )
        }
    }

    private fun setupObservers() {
        viewModel.mentorings.observe(this) { mentorings ->
            renderMentorings(
                mentorings
            )
        }

        viewModel.message.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.clearMessage()
            }
        }
    }

    private fun setupClicks() {
        findViewById<View>(
            R.id.btnFindMentor
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    FindMentorActivity::class.java
                )
            )
        }
    }

    private fun setupNotification() {
        NotificationBadgeHelper.setup(
            this,
            sessionManager,
            R.id.notificationContainer,
            R.id.tvNotificationBadge
        )
    }

    private fun renderMentorings(
        mentorings: List<StudentMentoringItem>
    ) {
        val container =
            findViewById<LinearLayout>(
                R.id.mentorChatContainer
            )

        container.removeAllViews()

        if (mentorings.isEmpty()) {
            val empty =
                TextView(this)

            empty.text =
                "You don't have a mentor yet."

            empty.gravity =
                Gravity.CENTER

            empty.setTextColor(
                Color.parseColor(
                    "#94A3B8"
                )
            )

            empty.setPadding(
                0,
                dp(40),
                0,
                dp(40)
            )

            container.addView(
                empty
            )

            return
        }

        mentorings.forEach { mentoring ->
            container.addView(
                createMentorCard(
                    mentoring
                )
            )
        }
    }

    private fun createMentorCard(
        mentoring: StudentMentoringItem
    ): View {
        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.HORIZONTAL

        card.gravity =
            Gravity.CENTER_VERTICAL

        card.setPadding(
            dp(12),
            dp(12),
            dp(12),
            dp(12)
        )

        card.setBackgroundResource(
            R.drawable.bg_certificate_card
        )

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.bottomMargin =
            dp(8)

        card.layoutParams =
            cardParams

        val avatarContainer =
            FrameLayout(this)

        val avatarParams =
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            )

        avatarParams.marginEnd =
            dp(12)

        avatarContainer.layoutParams =
            avatarParams

        val avatarImage =
            ImageView(this)

        avatarImage.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        avatarImage.scaleType =
            ImageView.ScaleType.CENTER_CROP

        avatarImage.setBackgroundResource(
            R.drawable.bg_avatar_circle
        )

        avatarImage.clipToOutline =
            true

        val avatarInitial =
            TextView(this)

        avatarInitial.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        avatarInitial.gravity =
            Gravity.CENTER

        avatarInitial.setBackgroundResource(
            R.drawable.bg_avatar_circle
        )

        avatarInitial.setTextColor(
            Color.parseColor(
                "#2F6FED"
            )
        )

        avatarInitial.textSize =
            14f

        avatarInitial.setTypeface(
            null,
            Typeface.BOLD
        )

        avatarContainer.addView(
            avatarImage
        )

        avatarContainer.addView(
            avatarInitial
        )

        AvatarHelper.show(
            imageView =
                avatarImage,
            initialView =
                avatarInitial,
            imagePath =
                mentoring.mentor_profile_image,
            name =
                mentoring.mentor_name
        )

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.layoutParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        val name =
            TextView(this)

        name.text =
            mentoring.mentor_name
                ?: "Mentor"

        name.textSize =
            14f

        name.setTypeface(
            null,
            Typeface.BOLD
        )

        name.setTextColor(
            Color.parseColor(
                "#0F172A"
            )
        )

        val course =
            TextView(this)

        course.text =
            if (
                mentoring.include_final_project == 1
            ) {
                "${mentoring.course_name} · Session ${mentoring.start_session} to Final project"
            } else {
                "${mentoring.course_name} · Sessions ${mentoring.start_session}-${mentoring.end_session}"
            }

        course.textSize =
            11f

        course.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        val status =
            TextView(this)

        status.text =
            when (
                mentoring.status
            ) {
                "active" ->
                    "Mentoring active"

                "completed" ->
                    "Mentoring completed"

                "expired" ->
                    "Mentoring ended"

                else ->
                    mentoring.status
            }

        status.textSize =
            11f

        status.setTextColor(
            if (
                mentoring.status == "active"
            ) {
                Color.parseColor(
                    "#64748B"
                )
            } else {
                Color.parseColor(
                    "#94A3B8"
                )
            }
        )

        content.addView(
            name
        )

        content.addView(
            course
        )

        content.addView(
            status
        )

        card.addView(
            avatarContainer
        )

        card.addView(
            content
        )

        card.setOnClickListener {
            val intent =
                Intent(
                    this,
                    StudentChatActivity::class.java
                )

            intent.putExtra(
                "MENTORING_ID",
                mentoring.mentoring_id
            )

            intent.putExtra(
                "COURSE_ID",
                mentoring.course_id
            )

            intent.putExtra(
                "PARTNER_NAME",
                mentoring.mentor_name
            )

            intent.putExtra(
                "PROFILE_IMAGE",
                mentoring.mentor_profile_image
            )

            intent.putExtra(
                "COURSE_NAME",
                mentoring.course_name
            )

            intent.putExtra(
                "START_SESSION",
                mentoring.start_session
            )

            intent.putExtra(
                "END_SESSION",
                mentoring.end_session
            )

            intent.putExtra(
                "INCLUDE_FINAL_PROJECT",
                mentoring.include_final_project
            )

            startActivity(
                intent
            )
        }

        return card
    }

    private fun setupNavigation() {
        findViewById<View>(
            R.id.navCourse
        ).setOnClickListener {
            val intent =
                Intent(
                    this,
                    StudentHomeActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(
                intent
            )

            finish()
        }

        findViewById<View>(
            R.id.navMentor
        ).setOnClickListener {
        }

        findViewById<View>(
            R.id.navCalendar
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentCalendarActivity::class.java
                )
            )

            finish()
        }

        findViewById<View>(
            R.id.navProfile
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentProfileActivity::class.java
                )
            )
        }
    }

    private fun dp(
        value: Int
    ): Int {
        return (
                value *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}