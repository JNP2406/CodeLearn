package com.example.codelearn.model

data class MentorDashboardResponse(
    val active_count: Int,
    val max_active: Int,
    val active_mentorings: List<ActiveMentoring>,
    val pending_requests: List<MentoringRequest>
)

data class ActiveMentoring(
    val mentoring_id: Int,
    val student_id: Int,
    val student_name: String?,
    val student_campus: String?,
    val student_profile_image: String?,
    val course_id: Int,
    val course_name: String,
    val start_session: Int,
    val end_session: Int,
    val include_final_project: Int,
    val start_date: String?,
    val deadline_at: String?,
    val status: String
)

data class MentoringRequest(
    val request_id: Int,
    val student_id: Int,
    val student_name: String?,
    val student_campus: String?,
    val student_profile_image: String?,
    val course_id: Int,
    val course_name: String,
    val start_session: Int,
    val end_session: Int,
    val include_final_project: Int,
    val status: String,
    val requested_at: String?
)

data class RespondMentoringRequest(
    val action: String
)

data class RespondMentoringResponse(
    val message: String,
    val mentoring_id: Int? = null,
    val deadline_at: String? = null
)

data class MentorListResponse(
    val mentors: List<MentorListItem>
)

data class MentorListItem(
    val id: Int,
    val name: String?,
    val campus: String?,
    val profile_image: String?,
    val cover_image: String?,
    val active_mentees: Int,
    val specializations: List<MentorSpecializationItem>
)

data class MentorSpecializationItem(
    val course_id: Int,
    val course_name: String
)

data class CreateMentoringRequest(
    val mentor_id: Int,
    val course_id: Int,
    val from_session: Int,
    val to_session: Int,
    val include_final_project: Boolean
)

data class CreateMentoringResponse(
    val message: String,
    val request_id: Int
)

data class StudentMentoringListResponse(
    val mentorings: List<StudentMentoringItem>
)

data class StudentMentoringItem(
    val mentoring_id: Int,
    val mentor_id: Int,
    val mentor_name: String?,
    val mentor_campus: String?,
    val mentor_profile_image: String?,
    val course_id: Int,
    val course_name: String,
    val start_session: Int,
    val end_session: Int,
    val include_final_project: Int,
    val start_at: String?,
    val deadline_at: String?,
    val ended_at: String?,
    val status: String
)

data class MentoringStatusResponse(
    val mentoring: MentoringStatusItem,
    val can_chat: Boolean
)

data class MentoringStatusItem(
    val id: Int,
    val student_id: Int,
    val mentor_id: Int,
    val course_id: Int,
    val from_session: Int,
    val to_session: Int,
    val include_final_project: Int,
    val start_at: String?,
    val deadline_at: String?,
    val ended_at: String?,
    val status: String
)