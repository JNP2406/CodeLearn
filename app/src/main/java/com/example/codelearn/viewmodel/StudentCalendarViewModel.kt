package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.StudentCalendarRepository
import com.example.codelearn.model.StudentCalendarEvent
import com.example.codelearn.model.StudentMentoringItem
import kotlinx.coroutines.launch
import java.time.LocalDate

class StudentCalendarViewModel : ViewModel() {

    private val repository =
        StudentCalendarRepository()

    private val _events =
        MutableLiveData<List<StudentCalendarEvent>>()

    val events:
            LiveData<List<StudentCalendarEvent>> =
        _events

    private val _activeMentorings =
        MutableLiveData<List<StudentMentoringItem>>()

    val activeMentorings:
            LiveData<List<StudentMentoringItem>> =
        _activeMentorings

    private val _message =
        MutableLiveData<String?>()

    val message:
            LiveData<String?> =
        _message

    fun loadCalendar(
        token: String
    ) {
        viewModelScope.launch {
            val result =
                mutableListOf<StudentCalendarEvent>()

            loadMentoringEvents(
                token,
                result
            )

            loadSessionEvents(
                token,
                result
            )

            _events.value =
                result.sortedBy {
                    it.date
                }
        }
    }

    private suspend fun loadMentoringEvents(
        token: String,
        result: MutableList<StudentCalendarEvent>
    ) {
        try {
            val response =
                repository.getStudentMentorings(
                    token
                )

            if (!response.isSuccessful) {
                _activeMentorings.value =
                    emptyList()

                return
            }

            val mentorings =
                response.body()
                    ?.mentorings
                    ?: emptyList()

            _activeMentorings.value =
                mentorings.filter {
                    it.status == "active"
                }

            mentorings.forEach { mentoring ->
                val eventDate =
                    when (
                        mentoring.status
                    ) {
                        "active" ->
                            parseDate(
                                mentoring.deadline_at
                            )

                        "completed" ->
                            parseDate(
                                mentoring.ended_at
                            )

                        "expired" ->
                            parseDate(
                                mentoring.ended_at
                            )
                                ?: parseDate(
                                    mentoring.deadline_at
                                )

                        else ->
                            null
                    }

                if (eventDate == null) {
                    return@forEach
                }

                val title =
                    when (
                        mentoring.status
                    ) {
                        "active" ->
                            "${mentoring.course_name} Mentoring Ends"

                        "completed" ->
                            "${mentoring.course_name} Mentoring Completed"

                        "expired" ->
                            "${mentoring.course_name} Mentoring Expired"

                        else ->
                            "${mentoring.course_name} Mentoring"
                    }

                val subtitle =
                    when (
                        mentoring.status
                    ) {
                        "active" ->
                            "Mentoring with ${mentoring.mentor_name ?: "Mentor"} ends"

                        "completed" ->
                            "Mentoring with ${mentoring.mentor_name ?: "Mentor"} completed"

                        "expired" ->
                            "Mentoring with ${mentoring.mentor_name ?: "Mentor"} expired"

                        else ->
                            "Mentoring with ${mentoring.mentor_name ?: "Mentor"}"
                    }

                result.add(
                    StudentCalendarEvent(
                        date =
                            eventDate,
                        title =
                            title,
                        subtitle =
                            subtitle,
                        type =
                            "mentoring"
                    )
                )
            }

        } catch (
            e: Exception
        ) {
            _activeMentorings.value =
                emptyList()

            _message.value =
                e.message
                    ?: "Failed to load mentoring schedule"
        }
    }

    private suspend fun loadSessionEvents(
        token: String,
        result: MutableList<StudentCalendarEvent>
    ) {
        for (
        courseId in 1..7
        ) {
            try {
                val response =
                    repository.getCourseProgress(
                        token,
                        courseId
                    )

                if (!response.isSuccessful) {
                    continue
                }

                val body =
                    response.body()
                        ?: continue

                body.sessions.forEach { session ->

                    if (
                        session.status == "active"
                    ) {
                        val deadline =
                            parseDate(
                                session.deadline_at
                            )

                        if (deadline != null) {
                            result.add(
                                StudentCalendarEvent(
                                    date =
                                        deadline,
                                    title =
                                        "${body.course_progress.course_name} · Session ${session.session_number} is due",
                                    subtitle =
                                        session.title,
                                    type =
                                        "session"
                                )
                            )
                        }
                    }

                    if (
                        session.status == "passed"
                    ) {
                        val completedDate =
                            parseDate(
                                session.completed_at
                            )

                        if (
                            completedDate != null
                        ) {
                            result.add(
                                StudentCalendarEvent(
                                    date =
                                        completedDate,
                                    title =
                                        "${body.course_progress.course_name} · Session ${session.session_number} Completed",
                                    subtitle =
                                        "${session.title} completed",
                                    type =
                                        "completed_session"
                                )
                            )
                        }
                    }
                }

            } catch (_: Exception) {
            }
        }
    }

    private fun parseDate(
        value: String?
    ): LocalDate? {
        if (
            value.isNullOrBlank() ||
            value.length < 10
        ) {
            return null
        }

        return try {
            LocalDate.parse(
                value.take(10)
            )
        } catch (_: Exception) {
            null
        }
    }

    fun clearMessage() {
        _message.value =
            null
    }
}