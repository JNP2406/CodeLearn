package com.example.codelearn.ui.student

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.remote.CourseDetail
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.CourseViewModel
import com.example.codelearn.viewmodel.ProgressViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CourseDetailActivity : AppCompatActivity() {

    private val courseViewModel: CourseViewModel by viewModels()
    private val progressViewModel: ProgressViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private lateinit var btnBackArea: LinearLayout
    private lateinit var basicCourseBanner: MaterialCardView
    private lateinit var imgCourseBanner: ImageView
    private lateinit var tvCourseIcon: TextView
    private lateinit var tvCourseName: TextView
    private lateinit var tvCourseDescription: TextView
    private lateinit var tvSessionCount: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var btnTakeCourse: MaterialButton

    private var courseId: Int = -1
    private var currentCourse: CourseDetail? = null

    private var courseAlreadyTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        courseId = intent.getIntExtra("COURSE_ID", -1)

        if (courseId == -1) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show()

            finish()
            return
        }

        bindViews()
        setupListeners()
        setupObservers()

        // Disable sementara sampai data course selesai load
        btnTakeCourse.isEnabled = false

        courseViewModel.loadCourseDetail(courseId)

        checkExistingProgress()
    }

    private fun bindViews() {
        btnBackArea = findViewById(R.id.btnBackArea)
        basicCourseBanner = findViewById(R.id.basicCourseBanner)
        imgCourseBanner = findViewById(R.id.imgCourseBanner)
        tvCourseIcon = findViewById(R.id.tvCourseIcon)
        tvCourseName = findViewById(R.id.tvCourseName)
        tvCourseDescription = findViewById(R.id.tvCourseDescription)
        tvSessionCount = findViewById(R.id.tvSessionCount)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        btnTakeCourse = findViewById(R.id.btnTakeCourse)
    }

    private fun setupListeners() {

        btnBackArea.setOnClickListener {
            finish()
        }

        btnTakeCourse.setOnClickListener {

            val course = currentCourse

            if (course == null) {
                Toast.makeText(this, "Course data is still loading", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            /*
             * Kalau course sudah pernah diambil,
             * langsung masuk ke Course Section.
             */
            if (courseAlreadyTaken) {
                openCourseSection()
                return@setOnClickListener
            }

            /*
             * Kalau belum pernah diambil,
             * tampilkan confirmation.
             */
            showTakeCourseConfirmation(course)
        }
    }

    private fun setupObservers() {

        // =========================
        // COURSE DETAIL
        // =========================

        courseViewModel.courseDetail.observe(this) { course ->

            if (course != null) {

                currentCourse = course

                showCourse(course)

                // Pastikan tombol aktif
                btnTakeCourse.isEnabled = true
            }
        }

        courseViewModel.errorMessage.observe(this) { message ->

            if (!message.isNullOrEmpty()) {

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }


        // =========================
        // CHECK EXISTING PROGRESS
        // =========================

        progressViewModel.courseProgress.observe(this) { result ->

            result.onSuccess {

                /*
                 * Kalau endpoint progress sukses,
                 * berarti student sudah mengambil course.
                 */
                courseAlreadyTaken = true

                btnTakeCourse.text = "Continue Course"

                btnTakeCourse.isEnabled = true
            }

            result.onFailure { error ->

                val message = error.message ?: ""

                /*
                 * 404 berarti student belum pernah
                 * mengambil course ini.
                 */
                if (message.contains("404")) {

                    courseAlreadyTaken = false

                    btnTakeCourse.text = "Take this Course"

                } else {

                    /*
                     * Kalau error lain, tetap biarkan
                     * tombol bisa digunakan.
                     */
                    courseAlreadyTaken = false

                    btnTakeCourse.text = "Take this Course"
                }

                btnTakeCourse.isEnabled = true
            }
        }


        // =========================
        // TAKE COURSE RESULT
        // =========================

        progressViewModel.takeCourseResult.observe(this) { result ->

            result.onSuccess {

                courseAlreadyTaken = true

                btnTakeCourse.isEnabled = true
                btnTakeCourse.text = "Continue Course"

                openCourseSection()
            }

            result.onFailure { error ->

                val message = error.message ?: "Failed to start course"

                btnTakeCourse.isEnabled = true

                /*
                 * 409 = course sudah pernah diambil.
                 */
                if (message.contains("409")) {

                    courseAlreadyTaken = true

                    btnTakeCourse.text = "Continue Course"

                    openCourseSection()

                } else {

                    btnTakeCourse.text = "Take this Course"

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkExistingProgress() {

        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {

            btnTakeCourse.isEnabled = true

            Toast.makeText(this, "Login session not found", Toast.LENGTH_LONG).show()

            return
        }

        progressViewModel.loadCourseProgress(token = token, courseId = courseId)
    }

    private fun showCourse(course: CourseDetail) {

        tvCourseName.text = course.name

        tvCourseDescription.text = course.description ?: "No description available."

        tvSessionCount.text = "${course.total_sessions} sessions"

        tvDifficulty.text = if (course.category == "basic") {
            "Beginner"
        } else {
            "Advanced"
        }

        showCourseVisual(course)
    }

    private fun showCourseVisual(course: CourseDetail) {

        if (course.category == "basic") {

            basicCourseBanner.visibility = View.VISIBLE

            imgCourseBanner.visibility = View.GONE

            tvCourseIcon.setTextColor(Color.WHITE)

            when (course.id) {

                1 -> {
                    tvCourseIcon.text = "Py"

                    basicCourseBanner.setCardBackgroundColor(Color.parseColor("#2F6FED"))
                }

                2 -> {
                    tvCourseIcon.text = "C"

                    basicCourseBanner.setCardBackgroundColor(Color.parseColor("#138A7E"))
                }

                3 -> {
                    tvCourseIcon.text = "Jv"

                    basicCourseBanner.setCardBackgroundColor(Color.parseColor("#C35B00"))
                }

                4 -> {
                    tvCourseIcon.text = "JS"

                    tvCourseIcon.setTextColor(Color.parseColor("#0F172A"))

                    basicCourseBanner.setCardBackgroundColor(Color.parseColor("#F59E0B"))
                }
            }

        } else {

            basicCourseBanner.visibility = View.GONE

            imgCourseBanner.visibility = View.VISIBLE

            when (course.id) {

                5 -> {
                    imgCourseBanner.setImageResource(R.drawable.banner_data_structures)
                }

                6 -> {
                    imgCourseBanner.setImageResource(R.drawable.banner_web_development)
                }

                7 -> {
                    imgCourseBanner.setImageResource(R.drawable.banner_data_structures)
                }
            }
        }
    }

    private fun showTakeCourseConfirmation(course: CourseDetail) {

        AlertDialog.Builder(this)
            .setTitle("Take this course?")
            .setMessage("You will start Session 1 of ${course.name}. " + "Each session must be completed within 1 week.")
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }
            .setPositiveButton("Take course") { _, _ ->

                val token = sessionManager.getToken()

                if (token.isNullOrEmpty()) {

                    Toast.makeText(this, "Login session not found", Toast.LENGTH_LONG).show()

                    return@setPositiveButton
                }

                /*
                 * Disable supaya tidak double click.
                 */
                btnTakeCourse.isEnabled = false
                btnTakeCourse.text = "Starting course..."

                progressViewModel.takeCourse(token = token, courseId = course.id)
            }
            .show()
    }

    private fun openCourseSection() {

        val intent = Intent(this, CourseSectionActivity::class.java)

        intent.putExtra("COURSE_ID", courseId)

        startActivity(intent)
    }
}