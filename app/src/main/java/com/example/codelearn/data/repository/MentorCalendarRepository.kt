package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient

class MentorCalendarRepository {

    private val api =
        ApiClient.apiService

    suspend fun getCalendar(
        token: String
    ) =
        api.getMentorCalendar(
            "Bearer $token"
        )
}