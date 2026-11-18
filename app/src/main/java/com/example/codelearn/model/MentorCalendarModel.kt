package com.example.codelearn.model

import java.time.LocalDate

data class MentorCalendarResponse(val mentorings: List<MentorCalendarItem>)

data class MentorCalendarItem(
    val mentoring_id: Int,
    val student_id: Int,
    val student_name: String?,
    val student_campus: String?,
    val student_profile_image: String?,
    val course_id: Int,
    val course_name: String,
    val start_session: Int,
    val end_session: Int?,
    val include_final_project: Int,
    val start_at: String?,
    val deadline_at: String?,
    val ended_at: String?,
    val status: String
)

data class MentorCalendarEvent(
    val date: LocalDate, val title: String, val subtitle: String, val type: String
)