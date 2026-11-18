package com.example.codelearn.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.util.NotificationBadgeHelper
import com.example.codelearn.viewmodel.CourseViewModel

class StudentHomeActivity : AppCompatActivity() {

    private val viewModel: CourseViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_home)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        setupCourseClicks()
        setupNavigation()
        setupNotification()
        setupObservers()

        viewModel.loadCourses()
    }

    override fun onResume() {
        super.onResume()

        if (::sessionManager.isInitialized && sessionManager.getToken() != null) {
            NotificationBadgeHelper.refresh(this, sessionManager, R.id.tvNotificationBadge)
        }
    }

    private fun setupObservers() {
        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupCourseClicks() {
        findViewById<View>(R.id.coursePython).setOnClickListener {
            openCourse(1)
        }

        findViewById<View>(R.id.courseC).setOnClickListener {
            openCourse(2)
        }

        findViewById<View>(R.id.courseJava).setOnClickListener {
            openCourse(3)
        }

        findViewById<View>(R.id.courseJavaScript).setOnClickListener {
            openCourse(4)
        }

        findViewById<View>(R.id.bannerDataStructures).setOnClickListener {
            openCourse(5)
        }

        findViewById<View>(R.id.bannerOop).setOnClickListener {
            openCourse(6)
        }

        findViewById<View>(R.id.bannerCompBio).setOnClickListener {
            openCourse(7)
        }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.navCourse).setOnClickListener {
            findViewById<View>(R.id.scrollContent).scrollTo(0, 0)
        }

        findViewById<View>(R.id.navMentor).setOnClickListener {
            startActivity(Intent(this, StudentMentorActivity::class.java))
        }

        findViewById<View>(R.id.navCalendar).setOnClickListener {
            startActivity(Intent(this, StudentCalendarActivity::class.java))
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }
    }

    private fun setupNotification() {
        NotificationBadgeHelper.setup(
            this, sessionManager, R.id.notificationContainer, R.id.tvNotificationBadge
        )
    }

    private fun openCourse(courseId: Int) {
        val intent = Intent(this, CourseDetailActivity::class.java)

        intent.putExtra("COURSE_ID", courseId)

        startActivity(intent)
    }
}