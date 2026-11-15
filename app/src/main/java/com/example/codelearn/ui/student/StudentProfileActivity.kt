package com.example.codelearn.ui.student

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.remote.StudentCertificateResponse
import com.example.codelearn.model.ProfileUser
import com.example.codelearn.ui.auth.SignInActivity
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class StudentProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private var currentProfile: ProfileUser? = null
    private var pendingUploadType: String? = null

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                uploadImage(uri)
            }
        }

    private val token: String
        get() = sessionManager.getToken() ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_student_profile
        )

        sessionManager =
            SessionManager(this)

        if (
            !sessionManager.isLoggedIn() ||
            token.isEmpty()
        ) {
            openSignIn()
            return
        }

        setupObservers()
        setupClicks()
        setupNavigation()

        viewModel.loadProfile(token)
        viewModel.loadCertificates(token)
    }

    override fun onResume() {
        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            token.isNotEmpty()
        ) {
            viewModel.loadProfile(token)
            viewModel.loadCertificates(token)
        }
    }

    private fun setupObservers() {
        viewModel.profile.observe(this) { profile ->
            currentProfile = profile
            renderProfile(profile)
        }

        viewModel.certificates.observe(this) { certificates ->
            renderCertificates(certificates)
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
            R.id.btnEditProfile
        ).setOnClickListener {
            showEditProfileDialog()
        }

        findViewById<View>(
            R.id.btnSignOut
        ).setOnClickListener {
            signOut()
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
            startActivity(
                Intent(
                    this,
                    StudentCalendarActivity::class.java
                )
            )

            finish()
        }
    }

    private fun renderProfile(
        profile: ProfileUser
    ) {
        findViewById<TextView>(
            R.id.tvName
        ).text =
            profile.name ?: "Student"

        findViewById<TextView>(
            R.id.tvCampus
        ).text =
            profile.campus ?: "No campus"

        AvatarHelper.show(
            imageView =
                findViewById<ImageView>(
                    R.id.ivProfile
                ),
            initialView =
                findViewById<TextView>(
                    R.id.tvProfileInitial
                ),
            imagePath =
                profile.profile_image,
            name =
                profile.name ?: "Student"
        )

        val coverImage =
            findViewById<ImageView>(
                R.id.ivCover
            )

        if (!profile.cover_image.isNullOrBlank()) {
            coverImage.load(
                buildUrl(
                    profile.cover_image
                )
            ) {
                crossfade(true)
            }
        } else {
            coverImage.setImageDrawable(null)

            coverImage.setBackgroundResource(
                R.drawable.bg_student_cover
            )
        }
    }

    private fun renderCertificates(
        certificates: List<StudentCertificateResponse>
    ) {
        val container =
            findViewById<LinearLayout>(
                R.id.certificateContainer
            )

        val emptyText =
            findViewById<TextView>(
                R.id.tvEmptyCertificates
            )

        container.removeAllViews()

        if (certificates.isEmpty()) {
            emptyText.visibility =
                View.VISIBLE

            return
        }

        emptyText.visibility =
            View.GONE

        certificates.forEach { certificate ->
            val card =
                TextView(this)

            card.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(64)
                ).apply {
                    bottomMargin = dp(10)
                }

            card.background =
                getDrawable(
                    R.drawable.bg_certificate_card
                )

            card.gravity =
                Gravity.CENTER_VERTICAL

            card.setPadding(
                dp(16),
                0,
                dp(16),
                0
            )

            card.text =
                "${certificate.course_name ?: "Course"} Certificate"

            card.setTextColor(
                android.graphics.Color.parseColor(
                    "#0F172A"
                )
            )

            card.textSize =
                14f

            card.setOnClickListener {
                openCertificate(
                    certificate
                )
            }

            container.addView(
                card
            )
        }
    }

    private fun showEditProfileDialog() {
        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        val view =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.dialog_edit_student_profile,
                    null
                )

        dialog.setContentView(
            view
        )

        dialog.window
            ?.setBackgroundDrawableResource(
                android.R.color.transparent
            )

        val etName =
            view.findViewById<EditText>(
                R.id.etName
            )

        val etCampus =
            view.findViewById<EditText>(
                R.id.etCampus
            )

        etName.setText(
            currentProfile?.name
                ?: sessionManager.getName()
                ?: ""
        )

        etCampus.setText(
            currentProfile?.campus
                ?: sessionManager.getCampus()
                ?: ""
        )

        view.findViewById<View>(
            R.id.btnChangePhoto
        ).setOnClickListener {
            pendingUploadType =
                "profile"

            imagePicker.launch(
                "image/*"
            )
        }

        view.findViewById<View>(
            R.id.btnChangeCover
        ).setOnClickListener {
            pendingUploadType =
                "cover"

            imagePicker.launch(
                "image/*"
            )
        }

        view.findViewById<View>(
            R.id.btnCancel
        ).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(
            R.id.btnSave
        ).setOnClickListener {
            val name =
                etName.text
                    .toString()
                    .trim()

            val campus =
                etCampus.text
                    .toString()
                    .trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Name is required",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.updateProfile(
                token,
                name,
                campus
            )

            dialog.dismiss()
        }

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (
                        resources
                            .displayMetrics
                            .widthPixels *
                                0.86
                        ).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.show()
    }

    private fun uploadImage(
        uri: Uri
    ) {
        val bytes =
            contentResolver
                .openInputStream(uri)
                ?.use {
                    it.readBytes()
                }
                ?: return

        val mimeType =
            contentResolver.getType(uri)
                ?: "image/jpeg"

        val extension =
            when (mimeType) {
                "image/png" ->
                    "png"

                "image/webp" ->
                    "webp"

                else ->
                    "jpg"
            }

        val requestBody =
            bytes.toRequestBody(
                mimeType.toMediaTypeOrNull()
            )

        if (
            pendingUploadType == "profile"
        ) {
            val imageView =
                findViewById<ImageView>(
                    R.id.ivProfile
                )

            val initialView =
                findViewById<TextView>(
                    R.id.tvProfileInitial
                )

            imageView.visibility =
                View.VISIBLE

            initialView.visibility =
                View.GONE

            imageView.load(uri)

            val part =
                MultipartBody.Part.createFormData(
                    "profile_image",
                    "profile-${System.currentTimeMillis()}.$extension",
                    requestBody
                )

            viewModel.uploadProfileImage(
                token,
                part
            )
        }

        if (
            pendingUploadType == "cover"
        ) {
            findViewById<ImageView>(
                R.id.ivCover
            ).load(uri)

            val part =
                MultipartBody.Part.createFormData(
                    "cover_image",
                    "cover-${System.currentTimeMillis()}.$extension",
                    requestBody
                )

            viewModel.uploadCoverImage(
                token,
                part
            )
        }

        pendingUploadType =
            null
    }

    private fun openCertificate(
        certificate: StudentCertificateResponse
    ) {
        val path =
            certificate.pdf_url

        if (path.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Certificate file unavailable",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val url =
            buildUrl(path)

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "Cannot open certificate",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun buildUrl(
        path: String
    ): String {
        if (
            path.startsWith(
                "http://"
            ) ||
            path.startsWith(
                "https://"
            )
        ) {
            return path
        }

        return "http://10.0.2.2:3001$path"
    }

    private fun signOut() {
        sessionManager.logout()
        openSignIn()
    }

    private fun openSignIn() {
        val intent =
            Intent(
                this,
                SignInActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(
            intent
        )

        finish()
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