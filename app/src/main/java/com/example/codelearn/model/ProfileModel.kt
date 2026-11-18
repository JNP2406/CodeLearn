package com.example.codelearn.model

data class ProfileResponse(val user: ProfileUser, val specializations: List<ProfileSpecialization>)

data class ProfileUser(
    val id: Int,
    val email: String,
    val role: String,
    val name: String?,
    val campus: String?,
    val profile_image: String?,
    val cover_image: String?,
    val is_demo: Int
)

data class ProfileSpecialization(val course_id: Int, val course_name: String)

data class UpdateProfileRequest(val name: String, val campus: String?)

data class ProfileUpdateResponse(val message: String)

data class ProfileImageResponse(val message: String, val profile_image: String?)

data class CoverImageResponse(val message: String, val cover_image: String?)

data class UpdateSpecializationsRequest(val course_ids: List<Int>)

data class SpecializationUpdateResponse(val message: String)