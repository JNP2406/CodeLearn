package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.model.UpdateProfileRequest
import com.example.codelearn.model.UpdateSpecializationsRequest
import okhttp3.MultipartBody

class ProfileRepository {

    private val api = ApiClient.apiService

    suspend fun getProfile(token: String) = api.getMyProfile("Bearer $token")

    suspend fun updateProfile(token: String, name: String, campus: String?) =
        api.updateMyProfile("Bearer $token", UpdateProfileRequest(name = name, campus = campus))

    suspend fun uploadProfileImage(token: String, image: MultipartBody.Part) =
        api.uploadProfileImage("Bearer $token", image)

    suspend fun uploadCoverImage(token: String, image: MultipartBody.Part) =
        api.uploadCoverImage("Bearer $token", image)

    suspend fun getCertificates(token: String) = api.getMyCertificates("Bearer $token")

    suspend fun updateSpecializations(token: String, courseIds: List<Int>) =
        api.updateSpecializations(
            "Bearer $token", UpdateSpecializationsRequest(course_ids = courseIds)
        )
}