package com.example.codelearn.data.remote

import com.example.codelearn.model.CoverImageResponse
import com.example.codelearn.model.CreateMentoringRequest
import com.example.codelearn.model.CreateMentoringResponse
import com.example.codelearn.model.MentorCalendarResponse
import com.example.codelearn.model.MentorCaseStudyResponse
import com.example.codelearn.model.MentorDashboardResponse
import com.example.codelearn.model.MentorListResponse
import com.example.codelearn.model.MentorReviewRequest
import com.example.codelearn.model.MentorReviewResponse
import com.example.codelearn.model.MentoringStatusResponse
import com.example.codelearn.model.NotificationActionResponse
import com.example.codelearn.model.NotificationListResponse
import com.example.codelearn.model.NotificationUnreadCountResponse
import com.example.codelearn.model.ProfileImageResponse
import com.example.codelearn.model.ProfileResponse
import com.example.codelearn.model.ProfileUpdateResponse
import com.example.codelearn.model.RespondMentoringRequest
import com.example.codelearn.model.RespondMentoringResponse
import com.example.codelearn.model.SpecializationUpdateResponse
import com.example.codelearn.model.StudentMentoringListResponse
import com.example.codelearn.model.UpdateProfileRequest
import com.example.codelearn.model.UpdateSpecializationsRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/courses")
    suspend fun getCourses(): Response<CourseListResponse>

    @GET("api/courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: Int): Response<CourseDetailResponse>

    @POST("api/progress/take-course/{courseId}")
    suspend fun takeCourse(
        @Header("Authorization") token: String, @Path("courseId") courseId: Int
    ): Response<TakeCourseResponse>

    @GET("api/progress/course/{courseId}")
    suspend fun getCourseProgress(
        @Header("Authorization") token: String, @Path("courseId") courseId: Int
    ): Response<CourseProgressResponse>

    @GET("api/materials/session/{sessionId}")
    suspend fun getSessionMaterials(@Path("sessionId") sessionId: Int): Response<MaterialListResponse>

    @GET("api/quiz/session/{sessionId}")
    suspend fun getQuiz(
        @Header("Authorization") token: String, @Path("sessionId") sessionId: Int
    ): Response<QuizResponse>

    @POST("api/quiz/session/{sessionId}/submit")
    suspend fun submitQuiz(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: Int,
        @Body request: QuizSubmitRequest
    ): Response<QuizSubmitResponse>

    @GET("api/quiz/session/{sessionId}/result")
    suspend fun getQuizResult(
        @Header("Authorization") token: String, @Path("sessionId") sessionId: Int
    ): Response<QuizResultResponse>

    @GET("api/case-study/course/{courseId}")
    suspend fun getCaseStudy(
        @Header("Authorization") token: String, @Path("courseId") courseId: Int
    ): Response<CaseStudyResponse>

    @POST("api/certificates/course/{courseId}/claim")
    suspend fun claimCertificate(
        @Header("Authorization") token: String, @Path("courseId") courseId: Int
    ): Response<ClaimCertificateResponse>

    @GET("api/certificates/my-certificates")
    suspend fun getMyCertificates(@Header("Authorization") token: String): Response<CertificateListResponse>

    @GET("api/profile/me")
    suspend fun getMyProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @PATCH("api/profile/me")
    suspend fun updateMyProfile(
        @Header("Authorization") token: String, @Body request: UpdateProfileRequest
    ): Response<ProfileUpdateResponse>

    @Multipart
    @POST("api/profile/me/profile-image")
    suspend fun uploadProfileImage(
        @Header("Authorization") token: String, @Part profileImage: MultipartBody.Part
    ): Response<ProfileImageResponse>

    @Multipart
    @POST("api/profile/me/cover-image")
    suspend fun uploadCoverImage(
        @Header("Authorization") token: String, @Part coverImage: MultipartBody.Part
    ): Response<CoverImageResponse>

    @PUT("api/profile/me/specializations")
    suspend fun updateSpecializations(
        @Header("Authorization") token: String, @Body request: UpdateSpecializationsRequest
    ): Response<SpecializationUpdateResponse>

    @GET("api/mentorings/mentor/dashboard")
    suspend fun getMentorDashboard(@Header("Authorization") token: String): Response<MentorDashboardResponse>

    @PATCH("api/mentorings/requests/{requestId}/respond")
    suspend fun respondMentoringRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int,
        @Body request: RespondMentoringRequest
    ): Response<RespondMentoringResponse>

    @GET("api/mentorings/student/mentors")
    suspend fun getAvailableMentors(@Header("Authorization") token: String): Response<MentorListResponse>

    @POST("api/mentorings/student/requests")
    suspend fun createMentoringRequest(
        @Header("Authorization") token: String, @Body request: CreateMentoringRequest
    ): Response<CreateMentoringResponse>

    @GET("api/mentorings/student/dashboard")
    suspend fun getStudentMentorings(@Header("Authorization") token: String): Response<StudentMentoringListResponse>

    @GET("api/case-study/mentor/mentoring/{mentoringId}")
    suspend fun getMentorCaseStudy(
        @Header("Authorization") token: String, @Path("mentoringId") mentoringId: Int
    ): Response<MentorCaseStudyResponse>

    @PATCH("api/case-study/reviews/{reviewId}")
    suspend fun updateMentorCaseStudyReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Int,
        @Body request: MentorReviewRequest
    ): Response<MentorReviewResponse>

    @GET("api/mentorings/{mentoringId}/status")
    suspend fun getMentoringStatus(
        @Header("Authorization") token: String, @Path("mentoringId") mentoringId: Int
    ): Response<MentoringStatusResponse>

    @GET("api/mentorings/mentor/calendar")
    suspend fun getMentorCalendar(@Header("Authorization") token: String): Response<MentorCalendarResponse>

    @GET("api/notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<NotificationListResponse>

    @GET("api/notifications/unread-count")
    suspend fun getNotificationUnreadCount(@Header("Authorization") token: String): Response<NotificationUnreadCountResponse>

    @PATCH("api/notifications/read-all")
    suspend fun markAllNotificationsRead(@Header("Authorization") token: String): Response<NotificationActionResponse>

    @PATCH("api/notifications/{notificationId}/read")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String, @Path("notificationId") notificationId: Int
    ): Response<NotificationActionResponse>

}

data class HealthResponse(val message: String)

data class RegisterRequest(
    val email: String, val password: String, val role: String, val name: String, val campus: String
)

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(val message: String?, val token: String?, val user: UserResponse?)

data class UserResponse(
    val id: Int,
    val email: String,
    val role: String,
    val name: String,
    val campus: String?,
    val is_demo: Boolean
)

data class CourseListResponse(val message: String, val courses: List<CourseResponse>)

data class CourseDetailResponse(val message: String, val course: CourseDetail)

data class CourseResponse(
    val id: Int,
    val name: String,
    val category: String,
    val description: String?,
    val total_sessions: Int,
    val image_url: String?
)

data class CourseDetail(
    val id: Int,
    val name: String,
    val category: String,
    val description: String?,
    val total_sessions: Int,
    val image_url: String?,
    val sessions: List<SessionResponse>
)

data class SessionResponse(
    val id: Int,
    val course_id: Int,
    val session_number: Int,
    val title: String,
    val description: String?
)

data class TakeCourseResponse(
    val message: String,
    val course_progress_id: Int?,
    val course: TakeCourseInfo?,
    val current_session: SessionProgressResponse?,
    val progress: ExistingCourseProgress?
)

data class TakeCourseInfo(val id: Int, val name: String)

data class ExistingCourseProgress(val id: Int, val current_session: Int, val started_at: String)

data class CourseProgressResponse(
    val message: String,
    val course_progress: CourseProgressInfo,
    val sessions: List<SessionProgressResponse>
)

data class CourseProgressInfo(
    val id: Int,
    val student_id: Int,
    val course_id: Int,
    val current_session: Int,
    val started_at: String,
    val course_name: String,
    val total_sessions: Int
)

data class SessionProgressResponse(
    val id: Int,
    val student_id: Int? = null,
    val session_id: Int,
    val session_number: Int,
    val title: String,
    val started_at: String,
    val deadline_at: String,
    val completed_at: String?,
    val status: String,
    val quiz_score: Int?
)

data class MaterialListResponse(
    val message: String, val session: MaterialSessionResponse, val materials: List<MaterialResponse>
)

data class MaterialSessionResponse(
    val id: Int, val course_id: Int, val session_number: Int, val title: String
)

data class MaterialResponse(
    val id: Int, val session_id: Int, val title: String, val type: String, val url: String
)

data class QuizResponse(
    val message: String,
    val session: QuizSessionResponse,
    val total_questions: Int,
    val questions: List<QuizQuestionResponse>
)

data class QuizSessionResponse(
    val id: Int, val course_id: Int, val session_number: Int, val title: String
)

data class QuizQuestionResponse(
    val id: Int, val question: String, val options: List<QuizOptionResponse>
)

data class QuizOptionResponse(val id: Int, val option_text: String)

data class QuizSubmitRequest(val answers: List<QuizAnswerRequest>)

data class QuizAnswerRequest(val question_id: Int, val option_id: Int)

data class QuizSubmitResponse(
    val message: String,
    val total_questions: Int,
    val correct_answers: Int,
    val score: Int,
    val passed: Boolean,
    val next_session_unlocked: Boolean,
    val next_session: NextSessionResponse?
)

data class NextSessionResponse(val id: Int, val session_number: Int, val title: String)

data class QuizResultResponse(
    val message: String,
    val session: QuizSessionResponse,
    val score: Int,
    val total_questions: Int,
    val correct_answers: Int,
    val passed: Boolean,
    val questions: List<QuizReviewQuestionResponse>
)

data class QuizReviewQuestionResponse(
    val id: Int,
    val question: String,
    val selected_option_id: Int?,
    val correct_option_id: Int?,
    val options: List<QuizOptionResponse>
)

data class CaseStudyResponse(
    val message: String,
    val case_study: CaseStudyDetailResponse,
    val mentoring: CaseStudyMentoringResponse?,
    val review: CaseStudyReviewResponse?
)

data class CaseStudyDetailResponse(
    val id: Int,
    val course_id: Int,
    val title: String,
    val scenario: String,
    val task_description: String
)

data class CaseStudyMentoringResponse(
    val id: Int,
    val mentor_id: Int,
    val status: String,
    val include_final_project: Int,
    val mentor_name: String
)

data class CaseStudyReviewResponse(
    val id: Int,
    val mentoring_id: Int,
    val case_study_id: Int,
    val status: String,
    val reviewed_at: String?
)

data class ClaimCertificateResponse(
    val message: String, val certificate: StudentCertificateResponse
)

data class CertificateListResponse(
    val message: String, val certificates: List<StudentCertificateResponse>
)

data class StudentCertificateResponse(
    val id: Int,
    val student_id: Int? = null,
    val mentor_id: Int? = null,
    val course_id: Int,
    val course_name: String? = null,
    val review_id: Int? = null,
    val certificate_id: String,
    val pdf_url: String?,
    val claimed_at: String?
)