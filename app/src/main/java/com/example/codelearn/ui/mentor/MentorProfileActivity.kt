package com.example.codelearn.ui.mentor

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.CheckBox
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
import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.model.ProfileSpecialization
import com.example.codelearn.model.ProfileUser
import com.example.codelearn.ui.auth.SignInActivity
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.util.AvatarHelper
import com.example.codelearn.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MentorProfileActivity : AppCompatActivity() {

    private val viewModel:
            ProfileViewModel by viewModels()

    private lateinit var sessionManager:
            SessionManager

    private var currentProfile:
            ProfileUser? = null

    private var currentSpecializations:
            List<ProfileSpecialization> = emptyList()

    private var pendingUploadType:
            String? = null

    private val token: String
        get() =
            sessionManager.getToken() ?: ""

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                uploadImage(uri)
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_mentor_profile
        )
        ActivityConfig.config(window)

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
    }

    override fun onResume() {
        super.onResume()

        if (
            ::sessionManager.isInitialized &&
            token.isNotEmpty()
        ) {
            viewModel.loadProfile(token)
        }
    }

    private fun setupObservers() {
        viewModel.profile.observe(this) {
            currentProfile =
                it

            renderProfile(
                it
            )
        }

        viewModel.specializations.observe(this) {
            currentSpecializations =
                it

            renderSpecializations(
                it
            )
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
            R.id.btnEditProfile
        ).setOnClickListener {
            showEditDialog()
        }

        findViewById<View>(
            R.id.btnAddCertificate
        ).setOnClickListener {
            Toast.makeText(
                this,
                "Mentor certificate backend will be connected later",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(
            R.id.btnSignOut
        ).setOnClickListener {
            sessionManager.logout()
            openSignIn()
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
            startActivity(
                Intent(
                    this,
                    MentorCalendarActivity::class.java
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
            profile.name ?: "Mentor"

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
                profile.name ?: "Mentor"
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
            coverImage.setBackgroundColor(
                android.graphics.Color.parseColor(
                    "#5A43E8"
                )
            )
        }
    }

    private fun renderSpecializations(
        specializations:
        List<ProfileSpecialization>
    ) {
        val container =
            findViewById<LinearLayout>(
                R.id.specializationContainer
            )

        container.removeAllViews()

        if (specializations.isEmpty()) {
            val empty =
                TextView(this)

            empty.text =
                "No specialization"

            empty.setTextColor(
                android.graphics.Color.GRAY
            )

            container.addView(
                empty
            )

            return
        }

        specializations.forEach {
            val tag =
                TextView(this)

            tag.text =
                it.course_name

            tag.setTextColor(
                android.graphics.Color.parseColor(
                    "#2F6FED"
                )
            )

            tag.setBackgroundResource(
                R.drawable.bg_role_badge
            )

            tag.setPadding(
                dp(10),
                dp(5),
                dp(10),
                dp(5)
            )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.marginEnd =
                dp(8)

            tag.layoutParams =
                params

            container.addView(
                tag
            )
        }
    }

    private fun showEditDialog() {
        val dialog =
            Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        val view =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.dialog_edit_mentor_profile,
                    null
                )

        dialog.setContentView(
            view
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

        val checkboxes =
            mapOf(
                1 to view.findViewById<CheckBox>(
                    R.id.checkPython
                ),
                2 to view.findViewById<CheckBox>(
                    R.id.checkC
                ),
                3 to view.findViewById<CheckBox>(
                    R.id.checkJava
                ),
                4 to view.findViewById<CheckBox>(
                    R.id.checkJavaScript
                ),
                5 to view.findViewById<CheckBox>(
                    R.id.checkDataStructures
                ),
                6 to view.findViewById<CheckBox>(
                    R.id.checkOop
                ),
                7 to view.findViewById<CheckBox>(
                    R.id.checkCompBio
                )
            )

        currentSpecializations.forEach {
            checkboxes[
                it.course_id
            ]?.isChecked =
                true
        }

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

            if (name.isBlank()) {
                Toast.makeText(
                    this,
                    "Name is required",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedCourseIds =
                checkboxes
                    .filter {
                        it.value.isChecked
                    }
                    .keys
                    .toList()

            viewModel.updateProfile(
                token,
                name,
                campus
            )

            viewModel.updateSpecializations(
                token,
                selectedCourseIds
            )

            dialog.dismiss()
        }

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (
                        resources
                            .displayMetrics
                            .widthPixels *
                                0.88
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

        val body =
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

            imageView.load(
                uri
            )

            val part =
                MultipartBody.Part.createFormData(
                    "profile_image",
                    "mentor-profile-${System.currentTimeMillis()}.$extension",
                    body
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
            ).load(
                uri
            )

            val part =
                MultipartBody.Part.createFormData(
                    "cover_image",
                    "mentor-cover-${System.currentTimeMillis()}.$extension",
                    body
                )

            viewModel.uploadCoverImage(
                token,
                part
            )
        }

        pendingUploadType =
            null
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

        return "${ApiClient.BASE_URL}$path"
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