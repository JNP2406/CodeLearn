package com.example.codelearn.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.NotificationItem
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.NotificationViewModel

class NotificationActivity : AppCompatActivity() {

    private val viewModel: NotificationViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private val token: String
        get() = sessionManager.getToken() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        setupHeader()
        setupObservers()

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        viewModel.loadNotifications(token)
    }

    private fun setupHeader() {
        findViewById<TextView>(R.id.tvSubtitle).text = if (sessionManager.getRole() == "mentor") {
            "New requests and mentoring that has ended."
        } else {
            "Request updates, finished mentoring, and a reminder one day before a session ends."
        }
    }

    private fun setupObservers() {
        viewModel.notifications.observe(this) {
            renderNotifications(it)

            if (it.any { notification ->
                    notification.is_read == 0
                }) {
                viewModel.markAllRead(token)
            }
        }

        viewModel.message.observe(this) {
            if (!it.isNullOrBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                viewModel.clearMessage()
            }
        }
    }

    private fun renderNotifications(notifications: List<NotificationItem>) {
        val container = findViewById<LinearLayout>(R.id.notificationListContainer)

        val empty = findViewById<TextView>(R.id.tvEmptyNotifications)

        container.removeAllViews()

        if (notifications.isEmpty()) {
            empty.visibility = View.VISIBLE

            return
        }

        empty.visibility = View.GONE

        notifications.forEach {
            container.addView(createNotificationCard(it))
        }
    }

    private fun createNotificationCard(notification: NotificationItem): View {
        val card = LinearLayout(this)

        card.orientation = LinearLayout.HORIZONTAL

        card.gravity = Gravity.CENTER_VERTICAL

        card.setPadding(dp(10), dp(10), dp(10), dp(10))

        card.background = cardDrawable(notification.is_read == 0)

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(9)
        }

        val avatar = FrameLayout(this)

        avatar.layoutParams = LinearLayout.LayoutParams(dp(42), dp(42)).apply {
            marginEnd = dp(12)
        }

        val initial = TextView(this)

        initial.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        initial.gravity = Gravity.CENTER

        initial.text = getAvatarText(notification)

        initial.textSize = 12f

        initial.setTypeface(null, Typeface.BOLD)

        initial.setTextColor(Color.WHITE)

        initial.background = avatarDrawable(notification.type)

        avatar.addView(initial)

        val content = LinearLayout(this)

        content.orientation = LinearLayout.VERTICAL

        content.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val title = TextView(this)

        title.text = notification.title

        title.textSize = 13f

        title.setTextColor(Color.parseColor("#0F172A"))

        title.setTypeface(null, Typeface.BOLD)

        val message = TextView(this)

        message.text = notification.message

        message.textSize = 11f

        message.setTextColor(Color.parseColor("#64748B"))

        content.addView(title)

        content.addView(message)

        card.addView(avatar)

        card.addView(content)

        return card
    }

    private fun getAvatarText(notification: NotificationItem): String {
        if (notification.type == "session_deadline") {
            val marker = " of "

            val start = notification.title.indexOf(marker)

            val end = notification.title.lastIndexOf(" ends tomorrow")

            if (start >= 0 && end > start) {
                val course = notification.title.substring(start + marker.length, end)

                return when (course.lowercase()) {
                    "python" -> "Py"

                    "javascript" -> "JS"

                    "java" -> "J"

                    "c" -> "C"

                    else -> course.take(2).uppercase()
                }
            }
        }

        if (notification.type == "case_approved" || notification.type == "case_not_approved") {
            return "FP"
        }

        val name = when {
            notification.title.startsWith("New request from ") -> notification.title.removePrefix("New request from ")

            notification.title.startsWith("Mentoring with ") -> notification.title.removePrefix("Mentoring with ")
                .removeSuffix(" ended")

            notification.title.contains(" accepted your request") -> notification.title.substringBefore(
                " accepted your request"
            )

            notification.title.contains(" declined your request") -> notification.title.substringBefore(
                " declined your request"
            )

            else -> notification.title
        }

        val parts = name.trim().split(" ").filter {
            it.isNotBlank()
        }

        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()

            parts.isNotEmpty() -> parts[0].take(2).uppercase()

            else -> "N"
        }
    }

    private fun cardDrawable(unread: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()

            setColor(
                Color.parseColor(
                    if (unread) {
                        "#F8FBFF"
                    } else {
                        "#FFFFFF"
                    }
                )
            )

            setStroke(dp(1), Color.parseColor("#CBD5E1"))
        }
    }

    private fun avatarDrawable(type: String): GradientDrawable {
        val color = when (type) {
            "session_deadline" -> "#2F6FED"

            "mentoring_request" -> "#C85A00"

            "request_accepted" -> "#0F8A80"

            "request_declined" -> "#C85A00"

            "case_approved" -> "#0F8A80"

            "case_not_approved" -> "#C85A00"

            else -> "#4F46D8"
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL

            setColor(Color.parseColor(color))
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}