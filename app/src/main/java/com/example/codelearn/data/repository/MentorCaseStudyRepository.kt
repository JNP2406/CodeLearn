package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.model.MentorReviewRequest

class MentorCaseStudyRepository {

    private val api = ApiClient.apiService

    suspend fun getCaseStudy(token: String, mentoringId: Int) =
        api.getMentorCaseStudy("Bearer $token", mentoringId)

    suspend fun submitReview(token: String, reviewId: Int, status: String) =
        api.updateMentorCaseStudyReview(
            "Bearer $token", reviewId, MentorReviewRequest(status = status)
        )
}