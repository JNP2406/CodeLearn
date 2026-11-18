package com.example.codelearn.model

data class MentorCaseStudyResponse(
    val mentoring: MentorCaseStudyMentoring,
    val case_study: MentorCaseStudy,
    val review: MentorCaseStudyReview
)

data class MentorCaseStudyMentoring(
    val mentoring_id: Int,
    val student_id: Int,
    val student_name: String?,
    val student_campus: String?,
    val student_profile_image: String?,
    val mentor_id: Int,
    val course_id: Int,
    val course_name: String,
    val from_session: Int,
    val to_session: Int,
    val include_final_project: Int,
    val start_at: String?,
    val deadline_at: String?,
    val ended_at: String?,
    val status: String
)

data class MentorCaseStudy(
    val id: Int,
    val course_id: Int,
    val title: String,
    val scenario: String?,
    val task_description: String?,
    val created_at: String?
)

data class MentorCaseStudyReview(
    val id: Int,
    val mentoring_id: Int,
    val case_study_id: Int,
    val status: String,
    val reviewed_at: String?
)

data class MentorReviewRequest(val status: String)

data class MentorReviewResponse(val message: String, val status: String)