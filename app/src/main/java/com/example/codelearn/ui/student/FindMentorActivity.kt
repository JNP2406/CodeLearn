package com.example.codelearn.ui.student

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.model.MentorListItem
import com.example.codelearn.viewmodel.FindMentorViewModel

class FindMentorActivity : AppCompatActivity() {

    private val viewModel:
            FindMentorViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private var allMentors:
            List<MentorListItem> = emptyList()

    private var selectedCourseFilter:
            Int? = null

    private val token: String
        get() =
            sessionManager.getToken() ?: ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_find_mentor
        )

        sessionManager =
            SessionManager(this)

        setupObservers()
        setupSearch()
        setupFilters()

        findViewById<View>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }

        viewModel.loadMentors(token)
    }

    private fun setupObservers() {
        viewModel.mentors.observe(this) {
            allMentors = it
            filterMentors()
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

        viewModel.requestSuccess.observe(this) {
            if (it) {
                viewModel.loadMentors(token)
                viewModel.clearRequestSuccess()
            }
        }
    }

    private fun setupSearch() {
        findViewById<EditText>(
            R.id.etSearch
        ).addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    filterMentors()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun setupFilters() {
        val filters =
            listOf(
                null to "All",
                1 to "Python",
                2 to "C",
                3 to "Java",
                4 to "JavaScript",
                5 to "Data Structures",
                6 to "OOP",
                7 to "Computational Biology"
            )

        val container =
            findViewById<LinearLayout>(
                R.id.filterContainer
            )

        filters.forEach { filter ->
            val chip =
                TextView(this)

            chip.text =
                filter.second

            chip.setPadding(
                dp(14),
                dp(8),
                dp(14),
                dp(8)
            )

            chip.setTextColor(
                android.graphics.Color.parseColor(
                    "#2F6FED"
                )
            )

            chip.setBackgroundResource(
                R.drawable.bg_role_badge
            )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.marginEnd =
                dp(8)

            chip.layoutParams =
                params

            chip.setOnClickListener {
                selectedCourseFilter =
                    filter.first

                filterMentors()
            }

            container.addView(chip)
        }
    }

    private fun filterMentors() {
        val query =
            findViewById<EditText>(
                R.id.etSearch
            ).text
                .toString()
                .trim()
                .lowercase()

        val result =
            allMentors.filter { mentor ->
                val matchesName =
                    mentor.name
                        ?.lowercase()
                        ?.contains(query)
                        ?: query.isEmpty()

                val matchesCourse =
                    selectedCourseFilter == null ||
                            mentor.specializations.any {
                                    specialization ->
                                specialization.course_id ==
                                        selectedCourseFilter
                            }

                matchesName &&
                        matchesCourse
            }

        renderMentors(result)
    }

    private fun renderMentors(
        mentors: List<MentorListItem>
    ) {
        val container =
            findViewById<LinearLayout>(
                R.id.mentorContainer
            )

        container.removeAllViews()

        if (mentors.isEmpty()) {
            val empty =
                TextView(this)

            empty.text =
                "No mentors found"

            empty.gravity =
                android.view.Gravity.CENTER

            empty.setPadding(
                0,
                dp(30),
                0,
                dp(30)
            )

            empty.setTextColor(
                android.graphics.Color.parseColor(
                    "#94A3B8"
                )
            )

            container.addView(empty)

            return
        }

        mentors.forEach { mentor ->
            container.addView(
                createMentorCard(
                    mentor
                )
            )
        }
    }

    private fun createMentorCard(
        mentor: MentorListItem
    ): View {
        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(16),
            dp(16),
            dp(16),
            dp(16)
        )

        card.setBackgroundResource(
            R.drawable.bg_certificate_card
        )

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin =
            dp(12)

        card.layoutParams =
            params

        val name =
            TextView(this)

        name.text =
            mentor.name ?: "Mentor"

        name.textSize =
            16f

        name.setTextColor(
            android.graphics.Color.parseColor(
                "#0F172A"
            )
        )

        name.setTypeface(
            name.typeface,
            android.graphics.Typeface.BOLD
        )

        val campus =
            TextView(this)

        campus.text =
            mentor.campus ?: "No campus"

        campus.textSize =
            12f

        campus.setTextColor(
            android.graphics.Color.parseColor(
                "#64748B"
            )
        )

        val specialization =
            TextView(this)

        specialization.text =
            mentor.specializations
                .joinToString(" · ") {
                    it.course_name
                }

        specialization.textSize =
            12f

        specialization.setTextColor(
            android.graphics.Color.parseColor(
                "#2F6FED"
            )
        )

        specialization.setPadding(
            0,
            dp(8),
            0,
            dp(10)
        )

        val capacity =
            TextView(this)

        capacity.text =
            "${mentor.active_mentees}/3 mentees"

        capacity.textSize =
            11f

        capacity.setTextColor(
            android.graphics.Color.parseColor(
                "#64748B"
            )
        )

        val request =
            TextView(this)

        request.text =
            "Request"

        request.gravity =
            android.view.Gravity.CENTER

        request.setTextColor(
            android.graphics.Color.WHITE
        )

        request.setBackgroundColor(
            android.graphics.Color.parseColor(
                "#2F6FED"
            )
        )

        request.setPadding(
            dp(14),
            dp(10),
            dp(14),
            dp(10)
        )

        request.setOnClickListener {
            showRequestDialog(
                mentor
            )
        }

        card.addView(name)
        card.addView(campus)
        card.addView(specialization)
        card.addView(capacity)
        card.addView(request)

        return card
    }

    private fun showRequestDialog(
        mentor: MentorListItem
    ) {
        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        val view =
            LayoutInflater.from(this)
                .inflate(
                    R.layout.dialog_request_mentor,
                    null
                )

        dialog.setContentView(view)

        view.findViewById<TextView>(
            R.id.tvMentor
        ).text =
            mentor.name ?: "Mentor"

        val courseSpinner =
            view.findViewById<Spinner>(
                R.id.spCourse
            )

        val fromSpinner =
            view.findViewById<Spinner>(
                R.id.spFromSession
            )

        val toSpinner =
            view.findViewById<Spinner>(
                R.id.spToSession
            )

        val finalProject =
            view.findViewById<CheckBox>(
                R.id.checkFinalProject
            )

        val courses =
            mentor.specializations

        val courseNames =
            courses.map {
                it.course_name
            }

        courseSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                courseNames
            )

        val sessions =
            listOf(
                "Session 1",
                "Session 2",
                "Session 3",
                "Session 4",
                "Session 5"
            )

        fromSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                sessions
            )

        toSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                sessions
            )

        view.findViewById<View>(
            R.id.btnCancel
        ).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(
            R.id.btnSend
        ).setOnClickListener {
            if (courses.isEmpty()) {
                Toast.makeText(
                    this,
                    "Mentor has no specialization",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val fromSession =
                fromSpinner.selectedItemPosition +
                        1

            val toSession =
                toSpinner.selectedItemPosition +
                        1

            if (
                toSession <
                fromSession
            ) {
                Toast.makeText(
                    this,
                    "End session cannot be before current session",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedCourse =
                courses[
                    courseSpinner.selectedItemPosition
                ]

            viewModel.sendRequest(
                token = token,
                mentorId = mentor.id,
                courseId =
                    selectedCourse.course_id,
                fromSession =
                    fromSession,
                toSession =
                    toSession,
                includeFinalProject =
                    finalProject.isChecked
            )

            dialog.dismiss()
        }

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (
                        resources
                            .displayMetrics
                            .widthPixels *
                                0.9
                        ).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.show()
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