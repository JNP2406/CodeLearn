package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.CourseDetailResponse
import com.example.codelearn.data.remote.CourseListResponse
import retrofit2.Response

class CourseRepository {

    private val apiService = ApiClient.apiService

    suspend fun getCourses(): Response<CourseListResponse> {
        return apiService.getCourses()
    }

    suspend fun getCourseDetail(
        courseId: Int
    ): Response<CourseDetailResponse> {
        return apiService.getCourseDetail(courseId)
    }
}