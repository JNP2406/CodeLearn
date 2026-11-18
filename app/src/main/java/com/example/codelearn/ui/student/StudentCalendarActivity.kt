package com.example.codelearn.ui.student

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
import com.example.codelearn.model.StudentCalendarEvent
import com.example.codelearn.model.StudentMentoringItem
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.util.NotificationBadgeHelper
import com.example.codelearn.viewmodel.StudentCalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class StudentCalendarActivity : AppCompatActivity() {

    private val viewModel:
            StudentCalendarViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private var displayedMonth:
            YearMonth = YearMonth.now()

    private var selectedDate:
            LocalDate = LocalDate.now()

    private var events:
            List<StudentCalendarEvent> =
        emptyList()

    private var activeMentorings:
            List<StudentMentoringItem> =
        emptyList()

    private val token: String
        get() =
            sessionManager.getToken() ?: ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_student_calendar
        )
        ActivityConfig.config(window)

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
        val monthFormatter =
            DateTimeFormatter.ofPattern(
                "MMMM yyyy",
                Locale.ENGLISH
            )

        findViewById<TextView>(
            R.id.tvMonth
        ).text =
            displayedMonth.format(
                monthFormatter
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
                grid.addView(
                    createEmptyDay()
                )

                continue
            }

            val date =
                displayedMonth.atDay(
                    dayNumber
                )

            grid.addView(
                createDayView(
                    date
                )
            )
        }
    }

    private fun createEmptyDay():
            View {
        val view =
            View(this)

        view.layoutParams =
            createGridParams()

        return view
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
            date.dayOfMonth
                .toString()

        day.textSize =
            12f

        val isSelected =
            date == selectedDate

        if (isSelected) {
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

        val hasEvent =
            events.any {
                it.date == date
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
            if (hasEvent) {
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

    private fun createGridParams():
            GridLayout.LayoutParams {
        val params =
            GridLayout.LayoutParams()

        params.width =
            0

        params.height =
            dp(52)

        params.columnSpec =
            GridLayout.spec(
                GridLayout.UNDEFINED,
                1f
            )

        return params
    }

    private fun renderSelectedDate() {
        val formatter =
            DateTimeFormatter.ofPattern(
                "EEE, d MMM yyyy",
                Locale.ENGLISH
            )

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
                        formatter
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

        name.setTextColor(
            Color.parseColor(
                "#0F172A"
            )
        )

        name.textSize =
            13f

        name.setTypeface(
            null,
            Typeface.BOLD
        )

        val role =
            TextView(this)

        role.text =
            "mentor"

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
            "${formatShortDate(mentoring.start_at)} – ${formatShortDate(mentoring.deadline_at)}"

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
            getDaysLeftText(
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
        event: StudentCalendarEvent
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

        val type =
            TextView(this)

        type.text =
            if (
                event.type == "mentoring"
            ) {
                "Mentoring"
            } else {
                "Course"
            }

        type.textSize =
            10f

        type.setTextColor(
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
            type
        )

        card.addView(
            title
        )

        card.addView(
            subtitle
        )

        return card
    }

    private fun formatShortDate(
        value: String?
    ): String {
        if (
            value.isNullOrBlank() ||
            value.length < 10
        ) {
            return "-"
        }

        return try {
            val date =
                LocalDate.parse(
                    value.take(10)
                )

            date.format(
                DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
                )
            )
        } catch (_: Exception) {
            "-"
        }
    }

    private fun getDaysLeftText(
        deadline: String?
    ): String {
        if (
            deadline.isNullOrBlank() ||
            deadline.length < 10
        ) {
            return "-"
        }

        return try {
            val date =
                LocalDate.parse(
                    deadline.take(10)
                )

            val days =
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    date
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

    private fun circleDrawable(
        fillColor: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape =
                GradientDrawable.OVAL

            setColor(
                Color.parseColor(
                    fillColor
                )
            )
        }
    }

    private fun roundedDrawable(
        fillColor: String,
        strokeColor: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape =
                GradientDrawable.RECTANGLE

            cornerRadius =
                dp(12).toFloat()

            setColor(
                Color.parseColor(
                    fillColor
                )
            )

            setStroke(
                dp(1),
                Color.parseColor(
                    strokeColor
                )
            )
        }
    }

    private fun roundedFillDrawable(
        fillColor: String
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape =
                GradientDrawable.RECTANGLE

            cornerRadius =
                dp(14).toFloat()

            setColor(
                Color.parseColor(
                    fillColor
                )
            )
        }
    }

    private fun setupNavigation() {
        findViewById<View>(
            R.id.navCourse
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentHomeActivity::class.java
                )
            )

            finish()
        }

        findViewById<View>(
            R.id.navMentor
        ).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentMentorActivity::class.java
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
                    StudentProfileActivity::class.java
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