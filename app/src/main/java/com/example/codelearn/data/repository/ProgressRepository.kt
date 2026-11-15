package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.CourseProgressResponse
import com.example.codelearn.data.remote.TakeCourseResponse
import retrofit2.Response

class ProgressRepository {

    private val apiService = ApiClient.apiService

    suspend fun takeCourse(
        token: String,
        courseId: Int
    ): Response<TakeCourseResponse> {

        return apiService.takeCourse(
            token = "Bearer $token",
            courseId = courseId
        )
    }

    suspend fun getCourseProgress(
        token: String,
        courseId: Int
    ): Response<CourseProgressResponse> {

        return apiService.getCourseProgress(
            token = "Bearer $token",
            courseId = courseId
        )
    }
}