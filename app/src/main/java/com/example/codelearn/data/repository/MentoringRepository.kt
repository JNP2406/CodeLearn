package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.model.CreateMentoringRequest
import com.example.codelearn.model.RespondMentoringRequest

class MentoringRepository {

    private val api = ApiClient.apiService

    suspend fun getMentorDashboard(token: String) = api.getMentorDashboard("Bearer $token")

    suspend fun respondRequest(token: String, requestId: Int, action: String) =
        api.respondMentoringRequest(
            "Bearer $token", requestId, RespondMentoringRequest(action = action)
        )

    suspend fun getAvailableMentors(token: String) = api.getAvailableMentors("Bearer $token")

    suspend fun createRequest(
        token: String,
        mentorId: Int,
        courseId: Int,
        fromSession: Int,
        toSession: Int,
        includeFinalProject: Boolean
    ) = api.createMentoringRequest(
        "Bearer $token", CreateMentoringRequest(
            mentor_id = mentorId,
            course_id = courseId,
            from_session = fromSession,
            to_session = toSession,
            include_final_project = includeFinalProject
        )
    )

    suspend fun getStudentMentorings(token: String) = api.getStudentMentorings("Bearer $token")

    suspend fun getMentoringStatus(token: String, mentoringId: Int) =
        api.getMentoringStatus("Bearer $token", mentoringId)
}