package com.example.codelearn.ui.mentor

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.ActiveMentoring
import com.example.codelearn.model.MentoringRequest
import com.example.codelearn.ui.auth.SignInActivity
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.util.NotificationBadgeHelper
import com.example.codelearn.viewmodel.MentorDashboardViewModel

class MentorChatsActivity : AppCompatActivity() {

    private val viewModel: MentorDashboardViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private val token: String
        get() = sessionManager.getToken() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mentor_chats)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn() || sessionManager.getRole() != "mentor") {
            openSignIn()
            return
        }

        setupObservers()
        setupNavigation()
        setupNotification()

        viewModel.loadDashboard(token)
    }

    override fun onResume() {
        super.onResume()

        if (::sessionManager.isInitialized && sessionManager.isLoggedIn() && sessionManager.getRole() == "mentor") {
            viewModel.loadDashboard(token)

            NotificationBadgeHelper.refresh(this, sessionManager, R.id.tvNotificationBadge)
        }
    }

    private fun setupObservers() {
        viewModel.dashboard.observe(this) { dashboard ->
            findViewById<TextView>(R.id.tvMenteeCount).text =
                "${dashboard.active_count}/${dashboard.max_active} mentees"

            renderActiveMentorings(dashboard.active_mentorings)

            renderPendingRequests(dashboard.pending_requests)
        }

        viewModel.message.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                viewModel.clearMessage()
            }
        }
    }

    private fun setupNotification() {
        NotificationBadgeHelper.setup(
            this, sessionManager, R.id.notificationContainer, R.id.tvNotificationBadge
        )
    }

    private fun renderActiveMentorings(mentorings: List<ActiveMentoring>) {
        val container = findViewById<LinearLayout>(R.id.ongoingContainer)

        container.removeAllViews()

        if (mentorings.isEmpty()) {
            container.addView(createEmptyText("No active mentees yet."))

            return
        }

        mentorings.forEach { mentoring ->
            val card = createMentoringCard(
                name = mentoring.student_name ?: "Student",
                imagePath = mentoring.student_profile_image,
                subtitle = if (mentoring.include_final_project == 1) {
                    "${mentoring.course_name} · Session ${mentoring.start_session} to Final project"
                } else {
                    "${mentoring.course_name} · Sessions ${mentoring.start_session}-${mentoring.end_session}"
                },
                message = "Open chat",
                actionText = null
            )

            card.setOnClickListener {
                val intent = Intent(this, MentorChatActivity::class.java)

                intent.putExtra("MENTORING_ID", mentoring.mentoring_id)

                intent.putExtra("PARTNER_NAME", mentoring.student_name)

                intent.putExtra("PROFILE_IMAGE", mentoring.student_profile_image)

                intent.putExtra("COURSE_NAME", mentoring.course_name)

                intent.putExtra("START_SESSION", mentoring.start_session)

                intent.putExtra("END_SESSION", mentoring.end_session)

                intent.putExtra("INCLUDE_FINAL_PROJECT", mentoring.include_final_project)

                startActivity(intent)
            }

            container.addView(card)
        }
    }

    private fun renderPendingRequests(requests: List<MentoringRequest>) {
        val container = findViewById<LinearLayout>(R.id.pendingContainer)

        container.removeAllViews()

        if (requests.isEmpty()) {
            container.addView(createEmptyText("No pending requests."))

            return
        }

        requests.forEach { request ->
            val card = createMentoringCard(
                name = request.student_name ?: "Student",
                imagePath = request.student_profile_image,
                subtitle = if (request.include_final_project == 1) {
                    "${request.course_name} · Session ${request.start_session} to Final project"
                } else {
                    "${request.course_name} · Sessions ${request.start_session}-${request.end_session}"
                },
                message = "",
                actionText = "Info"
            )

            card.setOnClickListener {
                showRequestDialog(request)
            }

            container.addView(card)
        }
    }

    private fun createMentoringCard(
        name: String, imagePath: String?, subtitle: String, message: String, actionText: String?
    ): View {
        val outer = LinearLayout(this)

        outer.orientation = LinearLayout.HORIZONTAL

        outer.gravity = Gravity.CENTER_VERTICAL

        outer.setPadding(dp(12), dp(12), dp(12), dp(12))

        outer.setBackgroundResource(R.drawable.bg_certificate_card)

        val outerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        outerParams.bottomMargin = dp(8)

        outer.layoutParams = outerParams

        val avatarContainer = FrameLayout(this)

        val avatarParams = LinearLayout.LayoutParams(dp(42), dp(42))

        avatarParams.marginEnd = dp(12)

        avatarContainer.layoutParams = avatarParams

        val avatarImage = ImageView(this)

        avatarImage.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        avatarImage.scaleType = ImageView.ScaleType.CENTER_CROP

        avatarImage.setBackgroundResource(R.drawable.bg_avatar_circle)

        avatarImage.clipToOutline = true

        val avatarInitial = TextView(this)

        avatarInitial.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        avatarInitial.gravity = Gravity.CENTER

        avatarInitial.setBackgroundResource(R.drawable.bg_avatar_circle)

        avatarInitial.setTextColor(Color.parseColor("#2F6FED"))

        avatarInitial.textSize = 14f

        avatarInitial.setTypeface(null, Typeface.BOLD)

        avatarContainer.addView(avatarImage)

        avatarContainer.addView(avatarInitial)

        AvatarHelper.show(
            imageView = avatarImage, initialView = avatarInitial, imagePath = imagePath, name = name
        )

        val content = LinearLayout(this)

        content.orientation = LinearLayout.VERTICAL

        content.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val titleView = TextView(this)

        titleView.text = name

        titleView.textSize = 14f

        titleView.setTextColor(Color.parseColor("#0F172A"))

        titleView.setTypeface(null, Typeface.BOLD)

        val subtitleView = TextView(this)

        subtitleView.text = subtitle

        subtitleView.textSize = 11f

        subtitleView.setTextColor(Color.parseColor("#64748B"))

        val messageView = TextView(this)

        messageView.text = message

        messageView.textSize = 11f

        messageView.setTextColor(Color.parseColor("#64748B"))

        content.addView(titleView)

        content.addView(subtitleView)

        if (message.isNotBlank()) {
            content.addView(messageView)
        }

        outer.addView(avatarContainer)

        outer.addView(content)

        if (!actionText.isNullOrBlank()) {
            val action = TextView(this)

            action.text = actionText

            action.gravity = Gravity.CENTER

            action.setTextColor(Color.parseColor("#2F6FED"))

            action.textSize = 11f

            action.setTypeface(null, Typeface.BOLD)

            action.setPadding(dp(12), dp(8), dp(12), dp(8))

            action.setBackgroundResource(R.drawable.bg_role_badge)

            outer.addView(action)
        }

        return outer
    }

    private fun showRequestDialog(request: MentoringRequest) {
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_mentoring_request, null)

        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.tvRequestTitle).text =
            "Request from ${request.student_name ?: "Student"}"

        view.findViewById<TextView>(R.id.tvRequestCourse).text = "Course: ${request.course_name}"

        val finalProjectText = if (request.include_final_project == 1) {
            " + Final Project"
        } else {
            ""
        }

        view.findViewById<TextView>(R.id.tvRequestSessions).text =
            "Help needed: Session ${request.start_session} to ${request.end_session}$finalProjectText"

        view.findViewById<View>(R.id.btnDecline).setOnClickListener {
            viewModel.respondRequest(
                token = token, requestId = request.request_id, action = "reject"
            )

            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnAccept).setOnClickListener {
            viewModel.respondRequest(
                token = token, requestId = request.request_id, action = "accept"
            )

            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.88).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun createEmptyText(value: String): TextView {
        val text = TextView(this)

        text.text = value

        text.gravity = Gravity.CENTER

        text.setTextColor(Color.parseColor("#94A3B8"))

        text.setPadding(0, dp(20), 0, dp(20))

        return text
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.navChats).setOnClickListener {}

        findViewById<View>(R.id.navCalendar).setOnClickListener {
            startActivity(Intent(this, MentorCalendarActivity::class.java))
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, MentorProfileActivity::class.java))
        }
    }

    private fun openSignIn() {
        val intent = Intent(this, SignInActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}