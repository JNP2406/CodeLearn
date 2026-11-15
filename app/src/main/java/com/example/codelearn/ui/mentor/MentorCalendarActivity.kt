package com.example.codelearn.ui.mentor

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.MentorCalendarEvent
import com.example.codelearn.model.MentorCalendarItem
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.util.NotificationBadgeHelper
import com.example.codelearn.viewmodel.MentorCalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class MentorCalendarActivity : AppCompatActivity() {

    private val viewModel:
            MentorCalendarViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private var displayedMonth:
            YearMonth = YearMonth.now()

    private var selectedDate:
            LocalDate = LocalDate.now()

    private var events:
            List<MentorCalendarEvent> =
        emptyList()

    private var activeMentorings:
            List<MentorCalendarItem> =
        emptyList()

    private val token: String
        get() =
            sessionManager.getToken() ?: ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_mentor_calendar
        )

        sessionManager =
            SessionManager(this)

        setupObservers()
        setupClicks()
        setupNavigation()
        setupNotification()

        renderCalendar()
        renderSelectedDate()
        renderMentorings()

        viewModel.loadCalendar(
            token
        )
    }

    override fun onResume() {
        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            token.isNotBlank()
        ) {
            viewModel.loadCalendar(
                token
            )

            NotificationBadgeHelper.refresh(
                this,
                sessionManager,
                R.id.tvNotificationBadge
            )
        }
    }

    private fun setupObservers() {
        viewModel.events.observe(this) {
            events =
                it

            renderCalendar()
            renderSelectedDate()
        }

        viewModel.activeMentorings.observe(this) {
            activeMentorings =
                it

            renderMentorings()
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
            R.id.btnPreviousMonth
        ).setOnClickListener {
            displayedMonth =
                displayedMonth.minusMonths(
                    1
                )

            selectedDate =
                displayedMonth.atDay(
                    1
                )

            renderCalendar()
            renderSelectedDate()
        }

        findViewById<View>(
            R.id.btnNextMonth
        ).setOnClickListener {
            displayedMonth =
                displayedMonth.plusMonths(
                    1
                )

            selectedDate =
                displayedMonth.atDay(
                    1
                )

            renderCalendar()
            renderSelectedDate()
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

    private fun renderCalendar() {
        findViewById<TextView>(
            R.id.tvMonth
        ).text =
            displayedMonth.format(
                DateTimeFormatter.ofPattern(
                    "MMMM yyyy",
                    Locale.ENGLISH
                )
            )

        val grid =
            findViewById<GridLayout>(
                R.id.calendarGrid
            )

        grid.removeAllViews()

        val firstDay =
            displayedMonth.atDay(
                1
            )

        val firstPosition =
            firstDay.dayOfWeek.value - 1

        val daysInMonth =
            displayedMonth.lengthOfMonth()

        for (
        position in 0 until 42
        ) {
            val dayNumber =
                position -
                        firstPosition +
                        1

            if (
                dayNumber < 1 ||
                dayNumber > daysInMonth
            ) {
                val empty =
                    View(this)

                empty.layoutParams =
                    createGridParams()

                grid.addView(
                    empty
                )

                continue
            }

            grid.addView(
                createDayView(
                    displayedMonth.atDay(
                        dayNumber
                    )
                )
            )
        }
    }

    private fun createDayView(
        date: LocalDate
    ): View {
        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.gravity =
            Gravity.CENTER

        container.layoutParams =
            createGridParams()

        val day =
            TextView(this)

        day.layoutParams =
            LinearLayout.LayoutParams(
                dp(34),
                dp(34)
            )

        day.gravity =
            Gravity.CENTER

        day.text =
            date.dayOfMonth.toString()

        day.textSize =
            12f

        if (
            date == selectedDate
        ) {
            day.setTextColor(
                Color.WHITE
            )

            day.setTypeface(
                null,
                Typeface.BOLD
            )

            day.background =
                circleDrawable(
                    "#2F6FED"
                )
        } else {
            day.setTextColor(
                Color.parseColor(
                    "#0F172A"
                )
            )
        }

        val dot =
            TextView(this)

        dot.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(10)
            )

        dot.gravity =
            Gravity.CENTER

        dot.text =
            if (
                events.any {
                    it.date == date
                }
            ) {
                "•"
            } else {
                ""
            }

        dot.textSize =
            13f

        dot.setTextColor(
            Color.parseColor(
                "#D97706"
            )
        )

        container.addView(
            day
        )

        container.addView(
            dot
        )

        container.setOnClickListener {
            selectedDate =
                date

            renderCalendar()
            renderSelectedDate()
        }

        return container
    }

    private fun renderSelectedDate() {
        val prefix =
            if (
                selectedDate == LocalDate.now()
            ) {
                "Today, "
            } else {
                ""
            }

        findViewById<TextView>(
            R.id.tvSelectedDate
        ).text =
            prefix +
                    selectedDate.format(
                        DateTimeFormatter.ofPattern(
                            "EEE, d MMM yyyy",
                            Locale.ENGLISH
                        )
                    )

        val container =
            findViewById<LinearLayout>(
                R.id.eventContainer
            )

        val empty =
            findViewById<TextView>(
                R.id.tvNoEvents
            )

        container.removeAllViews()

        val selectedEvents =
            events.filter {
                it.date == selectedDate
            }

        if (
            selectedEvents.isEmpty()
        ) {
            empty.visibility =
                View.VISIBLE

            return
        }

        empty.visibility =
            View.GONE

        selectedEvents.forEach {
            container.addView(
                createEventCard(
                    it
                )
            )
        }
    }

    private fun renderMentorings() {
        val container =
            findViewById<LinearLayout>(
                R.id.mentoringContainer
            )

        val empty =
            findViewById<TextView>(
                R.id.tvNoMentorings
            )

        container.removeAllViews()

        if (
            activeMentorings.isEmpty()
        ) {
            empty.visibility =
                View.VISIBLE

            return
        }

        empty.visibility =
            View.GONE

        activeMentorings.forEach {
            container.addView(
                createMentoringCard(
                    it
                )
            )
        }
    }

    private fun createMentoringCard(
        mentoring: MentorCalendarItem
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

        card.background =
            roundedDrawable(
                "#FFFFFF",
                "#CBD5E1"
            )

        card.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin =
                    dp(10)
            }

        val avatarContainer =
            FrameLayout(this)

        avatarContainer.layoutParams =
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            ).apply {
                marginEnd =
                    dp(12)
            }

        val image =
            ImageView(this)

        image.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        image.setBackgroundResource(
            R.drawable.bg_avatar_circle
        )

        image.clipToOutline =
            true

        val initial =
            TextView(this)

        initial.layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        initial.gravity =
            Gravity.CENTER

        initial.setBackgroundResource(
            R.drawable.bg_avatar_circle
        )

        initial.setTextColor(
            Color.parseColor(
                "#2F6FED"
            )
        )

        initial.textSize =
            14f

        initial.setTypeface(
            null,
            Typeface.BOLD
        )

        avatarContainer.addView(
            image
        )

        avatarContainer.addView(
            initial
        )

        AvatarHelper.show(
            imageView =
                image,
            initialView =
                initial,
            imagePath =
                mentoring.student_profile_image,
            name =
                mentoring.student_name
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
            mentoring.student_name
                ?: "Student"

        name.textSize =
            13f

        name.setTypeface(
            null,
            Typeface.BOLD
        )

        name.setTextColor(
            Color.parseColor(
                "#0F172A"
            )
        )

        val role =
            TextView(this)

        role.text =
            "mentee"

        role.textSize =
            10f

        role.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        val session =
            TextView(this)

        session.text =
            if (
                mentoring.include_final_project == 1
            ) {
                "${mentoring.course_name} · Session ${mentoring.start_session} to Final project"
            } else {
                "${mentoring.course_name} · Sessions ${mentoring.start_session}-${mentoring.end_session}"
            }

        session.textSize =
            11f

        session.setTextColor(
            Color.parseColor(
                "#475569"
            )
        )

        val period =
            TextView(this)

        period.text =
            "${formatDate(mentoring.start_at)} – ${formatDate(mentoring.deadline_at)}"

        period.textSize =
            10f

        period.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        content.addView(
            name
        )

        content.addView(
            role
        )

        content.addView(
            session
        )

        content.addView(
            period
        )

        val daysLeft =
            TextView(this)

        daysLeft.text =
            getDaysLeft(
                mentoring.deadline_at
            )

        daysLeft.gravity =
            Gravity.CENTER

        daysLeft.setPadding(
            dp(9),
            dp(5),
            dp(9),
            dp(5)
        )

        daysLeft.background =
            roundedFillDrawable(
                "#E8F0FF"
            )

        daysLeft.setTextColor(
            Color.parseColor(
                "#2F6FED"
            )
        )

        daysLeft.textSize =
            10f

        daysLeft.setTypeface(
            null,
            Typeface.BOLD
        )

        card.addView(
            avatarContainer
        )

        card.addView(
            content
        )

        card.addView(
            daysLeft
        )

        return card
    }

    private fun createEventCard(
        event: MentorCalendarEvent
    ): View {
        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(14),
            dp(12),
            dp(14),
            dp(12)
        )

        card.background =
            roundedDrawable(
                "#FFFFFF",
                "#CBD5E1"
            )

        card.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin =
                    dp(10)
            }

        val label =
            TextView(this)

        label.text =
            "Mentoring"

        label.textSize =
            10f

        label.setTextColor(
            Color.parseColor(
                "#2F6FED"
            )
        )

        val title =
            TextView(this)

        title.text =
            event.title

        title.textSize =
            13f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.parseColor(
                "#0F172A"
            )
        )

        val subtitle =
            TextView(this)

        subtitle.text =
            event.subtitle

        subtitle.textSize =
            11f

        subtitle.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        card.addView(
            label
        )

        card.addView(
            title
        )

        card.addView(
            subtitle
        )

        return card
    }

    private fun formatDate(
        value: String?
    ): String {
        if (
            value.isNullOrBlank() ||
            value.length < 10
        ) {
            return "-"
        }

        return try {
            LocalDate.parse(
                value.take(10)
            ).format(
                DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
                )
            )
        } catch (_: Exception) {
            "-"
        }
    }

    private fun getDaysLeft(
        value: String?
    ): String {
        if (
            value.isNullOrBlank() ||
            value.length < 10
        ) {
            return "-"
        }

        return try {
            val deadline =
                LocalDate.parse(
                    value.take(10)
                )

            val days =
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    deadline
                )

            when {
                days > 1 ->
                    "$days days left"

                days == 1L ->
                    "1 day left"

                days == 0L ->
                    "Ends today"

                else ->
                    "Ended"
            }

        } catch (_: Exception) {
            "-"
        }
    }

    private fun createGridParams():
            GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width =
                0

            height =
                dp(52)

            columnSpec =
                GridLayout.spec(
                    GridLayout.UNDEFINED,
                    1f
                )
        }
    }

    private fun circleDrawable(
        color: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape =
                GradientDrawable.OVAL

            setColor(
                Color.parseColor(
                    color
                )
            )
        }
    }

    private fun roundedDrawable(
        fill: String,
        stroke: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius =
                dp(12).toFloat()

            setColor(
                Color.parseColor(
                    fill
                )
            )

            setStroke(
                dp(1),
                Color.parseColor(
                    stroke
                )
            )
        }
    }

    private fun roundedFillDrawable(
        fill: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius =
                dp(14).toFloat()

            setColor(
                Color.parseColor(
                    fill
                )
            )
        }
    }

    private fun setupNavigation() {
        findViewById<View>(
            R.id.navChats
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MentorChatsActivity::class.java
                )
            )

            finish()
        }

        findViewById<View>(
            R.id.navCalendar
        ).setOnClickListener {
        }

        findViewById<View>(
            R.id.navProfile
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MentorProfileActivity::class.java
                )
            )

            finish()
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