package com.example.codelearn.ui.student

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.remote.CourseDetail
import com.example.codelearn.data.remote.MaterialResponse
import com.example.codelearn.data.remote.SessionProgressResponse
import com.example.codelearn.data.remote.SessionResponse
import com.example.codelearn.viewmodel.CourseViewModel
import com.example.codelearn.viewmodel.MaterialViewModel
import com.example.codelearn.viewmodel.ProgressViewModel
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CourseSectionActivity : AppCompatActivity() {

    private val courseViewModel:
            CourseViewModel by viewModels()

    private val progressViewModel:
            ProgressViewModel by viewModels()

    private val materialViewModel:
            MaterialViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private lateinit var btnBackArea:
            LinearLayout

    private lateinit var tvCourseName:
            TextView

    private lateinit var sessionTabsContainer:
            LinearLayout

    private lateinit var tvSessionTitle:
            TextView

    private lateinit var tvSessionSubtitle:
            TextView

    private lateinit var tvStartDate:
            TextView

    private lateinit var tvDeadlineDate:
            TextView

    private lateinit var materialContainer:
            LinearLayout

    private lateinit var cardSessionTest:
            MaterialCardView

    private lateinit var cardFinalProject:
            MaterialCardView

    private var courseId =
        -1

    private var currentCourse:
            CourseDetail? = null

    private var selectedSession:
            SessionResponse? = null

    private var progressSessions:
            List<SessionProgressResponse> =
        emptyList()

    private var isDemoAccount =
        false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_course_section
        )

        sessionManager =
            SessionManager(this)

        isDemoAccount =
            sessionManager.isDemo()

        courseId =
            intent.getIntExtra(
                "COURSE_ID",
                -1
            )

        if (courseId == -1) {

            Toast.makeText(
                this,
                "Course not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        bindViews()
        setupListeners()
        setupObservers()

        courseViewModel.loadCourseDetail(
            courseId
        )
    }

    override fun onResume() {

        super.onResume()

        loadProgress()
    }

    private fun bindViews() {

        btnBackArea =
            findViewById(
                R.id.btnBackArea
            )

        tvCourseName =
            findViewById(
                R.id.tvCourseName
            )

        sessionTabsContainer =
            findViewById(
                R.id.sessionTabsContainer
            )

        tvSessionTitle =
            findViewById(
                R.id.tvSessionTitle
            )

        tvSessionSubtitle =
            findViewById(
                R.id.tvSessionSubtitle
            )

        tvStartDate =
            findViewById(
                R.id.tvStartDate
            )

        tvDeadlineDate =
            findViewById(
                R.id.tvDeadlineDate
            )

        materialContainer =
            findViewById(
                R.id.materialContainer
            )

        cardSessionTest =
            findViewById(
                R.id.cardSessionTest
            )

        cardFinalProject =
            findViewById(
                R.id.cardFinalProject
            )
    }

    private fun setupListeners() {

        btnBackArea.setOnClickListener {

            finish()
        }

        cardSessionTest.setOnClickListener {

            val session =
                selectedSession

            if (session == null) {
                return@setOnClickListener
            }

            if (
                !isSessionUnlocked(
                    session.session_number
                )
            ) {

                Toast.makeText(
                    this,
                    "Session masih terkunci",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val progress =
                getProgressForSession(
                    session.session_number
                )

            val intent =
                Intent(
                    this,
                    QuizActivity::class.java
                )

            intent.putExtra(
                "SESSION_ID",
                session.id
            )

            intent.putExtra(
                "SESSION_NUMBER",
                session.session_number
            )

            intent.putExtra(
                "COURSE_NAME",
                currentCourse?.name ?: ""
            )

            intent.putExtra(
                "REVIEW_MODE",
                progress?.status == "passed"
            )

            startActivity(intent)
        }

        cardFinalProject.setOnClickListener {

            val session =
                selectedSession

            if (
                session == null ||
                session.session_number != 5
            ) {

                return@setOnClickListener
            }

            val intent =
                Intent(
                    this,
                    CaseStudyActivity::class.java
                )

            intent.putExtra(
                "COURSE_ID",
                courseId
            )

            startActivity(
                intent
            )
        }
    }

    private fun setupObservers() {

        courseViewModel.courseDetail
            .observe(this) { course ->

                if (course != null) {

                    currentCourse =
                        course

                    tvCourseName.text =
                        course.name

                    createSessionTabs(
                        course.sessions
                    )

                    if (
                        selectedSession == null &&
                        course.sessions.isNotEmpty()
                    ) {

                        selectSession(
                            course.sessions.first()
                        )
                    }
                }
            }

        progressViewModel.courseProgress
            .observe(this) { result ->

                result.onSuccess { response ->

                    progressSessions =
                        response.sessions

                    currentCourse?.let { course ->

                        createSessionTabs(
                            course.sessions
                        )
                    }

                    selectedSession?.let { session ->

                        selectSession(
                            session
                        )
                    }
                }

                result.onFailure { error ->

                    if (!isDemoAccount) {

                        Toast.makeText(
                            this,
                            error.message
                                ?: "Failed to load progress",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        materialViewModel.materials
            .observe(this) { result ->

                result.onSuccess { response ->

                    showMaterials(
                        response.materials
                    )
                }

                result.onFailure {

                    materialContainer.removeAllViews()

                    addEmptyMaterialMessage(
                        "No materials available yet."
                    )
                }
            }
    }

    private fun loadProgress() {

        val token =
            sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            return
        }

        progressViewModel.loadCourseProgress(
            token,
            courseId
        )
    }

    private fun createSessionTabs(
        sessions: List<SessionResponse>
    ) {

        sessionTabsContainer.removeAllViews()

        sessions.forEach { session ->

            val tab =
                TextView(this)

            val params =
                LinearLayout.LayoutParams(
                    dpToPx(40),
                    dpToPx(40)
                )

            params.marginEnd =
                dpToPx(8)

            tab.layoutParams =
                params

            tab.gravity =
                Gravity.CENTER

            tab.text =
                session.session_number.toString()

            tab.textSize =
                14f

            tab.setTypeface(
                tab.typeface,
                Typeface.BOLD
            )

            val unlocked =
                isSessionUnlocked(
                    session.session_number
                )

            if (unlocked) {

                tab.alpha =
                    1f

                tab.setBackgroundResource(
                    R.drawable.bg_session_inactive
                )

                tab.setTextColor(
                    getColor(
                        R.color.blue
                    )
                )

                tab.setOnClickListener {

                    selectSession(
                        session
                    )
                }

            } else {

                tab.alpha =
                    0.45f

                tab.setBackgroundResource(
                    R.drawable.bg_session_inactive
                )

                tab.setTextColor(
                    Color.parseColor(
                        "#94A3B8"
                    )
                )

                tab.setOnClickListener {

                    Toast.makeText(
                        this,
                        "Session ${session.session_number} masih terkunci",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            sessionTabsContainer.addView(
                tab
            )
        }

        selectedSession?.let {

            updateSessionTabs(
                it.session_number
            )
        }
    }

    private fun selectSession(
        session: SessionResponse
    ) {

        selectedSession =
            session

        tvSessionTitle.text =
            "Session ${session.session_number}"

        tvSessionSubtitle.text =
            session.title

        updateSessionTabs(
            session.session_number
        )

        showSessionDates(
            session
        )

        materialContainer.removeAllViews()

        addEmptyMaterialMessage(
            "Loading materials..."
        )

        materialViewModel.loadSessionMaterials(
            session.id
        )

        cardFinalProject.visibility =
            if (
                session.session_number == 5
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }
    }

    private fun showSessionDates(
        session: SessionResponse
    ) {

        val progress =
            getProgressForSession(
                session.session_number
            )

        if (progress != null) {

            tvStartDate.text =
                formatDate(
                    progress.started_at
                )

            tvDeadlineDate.text =
                formatDate(
                    progress.deadline_at
                )

        } else if (
            isDemoAccount
        ) {

            tvStartDate.text =
                "Demo"

            tvDeadlineDate.text =
                "Demo"

        } else {

            tvStartDate.text =
                "Locked"

            tvDeadlineDate.text =
                "Locked"
        }
    }

    private fun showMaterials(
        materials:
        List<MaterialResponse>
    ) {

        materialContainer.removeAllViews()

        if (materials.isEmpty()) {

            addEmptyMaterialMessage(
                "No materials available yet."
            )

            return
        }

        materials.forEach {

            addMaterialCard(
                it
            )
        }
    }

    private fun addMaterialCard(
        material:
        MaterialResponse
    ) {

        val card =
            MaterialCardView(this)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin =
            dpToPx(10)

        card.layoutParams =
            params

        card.radius =
            dpToPx(12)
                .toFloat()

        card.cardElevation =
            0f

        card.strokeWidth =
            dpToPx(1)

        card.strokeColor =
            Color.parseColor(
                "#CBD5E1"
            )

        card.setCardBackgroundColor(
            Color.WHITE
        )

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.setPadding(
            dpToPx(14),
            dpToPx(12),
            dpToPx(14),
            dpToPx(12)
        )

        val title =
            TextView(this)

        title.text =
            material.title

        title.textSize =
            14f

        title.setTypeface(
            title.typeface,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.parseColor(
                "#0F172A"
            )
        )

        val type =
            TextView(this)

        type.text =
            getMaterialTypeLabel(
                material.type
            )

        type.textSize =
            11f

        type.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        type.setPadding(
            0,
            dpToPx(4),
            0,
            0
        )

        content.addView(
            title
        )

        content.addView(
            type
        )

        card.addView(
            content
        )

        card.setOnClickListener {

            openMaterial(
                material
            )
        }

        materialContainer.addView(
            card
        )
    }

    private fun openMaterial(
        material:
        MaterialResponse
    ) {

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        material.url
                    )
                )

            startActivity(
                intent
            )

        } catch (
            e: Exception
        ) {

            Toast.makeText(
                this,
                "Cannot open material",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getMaterialTypeLabel(
        type:
        String
    ): String {

        return when (
            type.lowercase()
        ) {

            "pdf" ->
                "PDF Material"

            "youtube" ->
                "YouTube Video"

            "website" ->
                "Website"

            else ->
                "Learning Material"
        }
    }

    private fun addEmptyMaterialMessage(
        message:
        String
    ) {

        val text =
            TextView(this)

        text.text =
            message

        text.textSize =
            13f

        text.setTextColor(
            Color.parseColor(
                "#64748B"
            )
        )

        text.setPadding(
            0,
            dpToPx(8),
            0,
            dpToPx(8)
        )

        materialContainer.addView(
            text
        )
    }

    private fun updateSessionTabs(
        selected:
        Int
    ) {

        for (
        i in 0 until
                sessionTabsContainer.childCount
        ) {

            val tab =
                sessionTabsContainer
                    .getChildAt(i)
                        as TextView

            val sessionNumber =
                i + 1

            val unlocked =
                isSessionUnlocked(
                    sessionNumber
                )

            if (
                unlocked &&
                sessionNumber == selected
            ) {

                tab.setBackgroundResource(
                    R.drawable.bg_session_active
                )

                tab.setTextColor(
                    Color.WHITE
                )

                tab.alpha =
                    1f

            } else if (
                unlocked
            ) {

                tab.setBackgroundResource(
                    R.drawable.bg_session_inactive
                )

                tab.setTextColor(
                    getColor(
                        R.color.blue
                    )
                )

                tab.alpha =
                    1f

            } else {

                tab.setBackgroundResource(
                    R.drawable.bg_session_inactive
                )

                tab.setTextColor(
                    Color.parseColor(
                        "#94A3B8"
                    )
                )

                tab.alpha =
                    0.45f
            }
        }
    }

    private fun getProgressForSession(
        sessionNumber:
        Int
    ): SessionProgressResponse? {

        return progressSessions.find {

            it.session_number ==
                    sessionNumber
        }
    }

    private fun isSessionUnlocked(
        sessionNumber:
        Int
    ): Boolean {

        if (isDemoAccount) {
            return true
        }

        return getProgressForSession(
            sessionNumber
        ) != null
    }

    private fun formatDate(
        rawDate:
        String
    ): String {

        return try {

            val input =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.US
                )

            input.timeZone =
                TimeZone.getTimeZone(
                    "UTC"
                )

            val date =
                input.parse(
                    rawDate
                )

            val output =
                SimpleDateFormat(
                    "dd MMM yyyy, HH:mm",
                    Locale.US
                )

            output.timeZone =
                TimeZone.getDefault()

            if (date != null) {

                output.format(
                    date
                )

            } else {

                "-"
            }

        } catch (
            e: Exception
        ) {

            "-"
        }
    }

    private fun dpToPx(
        dp:
        Int
    ): Int {

        return (
                dp *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}