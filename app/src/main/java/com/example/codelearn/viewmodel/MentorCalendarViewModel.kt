package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentorCalendarRepository
import com.example.codelearn.model.MentorCalendarEvent
import com.example.codelearn.model.MentorCalendarItem
import kotlinx.coroutines.launch
import java.time.LocalDate

class MentorCalendarViewModel : ViewModel() {

    private val repository = MentorCalendarRepository()

    private val _events = MutableLiveData<List<MentorCalendarEvent>>()

    val events: LiveData<List<MentorCalendarEvent>> = _events

    private val _activeMentorings = MutableLiveData<List<MentorCalendarItem>>()

    val activeMentorings: LiveData<List<MentorCalendarItem>> = _activeMentorings

    private val _message = MutableLiveData<String?>()

    val message: LiveData<String?> = _message

    fun loadCalendar(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCalendar(token)

                if (!response.isSuccessful) {
                    _message.value = "Failed to load calendar"

                    return@launch
                }

                val mentorings = response.body()?.mentorings ?: emptyList()

                _activeMentorings.value = mentorings.filter {
                    it.status == "active"
                }

                val result = mutableListOf<MentorCalendarEvent>()

                mentorings.forEach { mentoring ->
                    val date = when (mentoring.status) {
                        "active" -> parseDate(mentoring.deadline_at)

                        "completed" -> parseDate(mentoring.ended_at)

                        "expired" -> parseDate(mentoring.ended_at)
                            ?: parseDate(mentoring.deadline_at)

                        else -> null
                    }

                    if (date == null) {
                        return@forEach
                    }

                    val student = mentoring.student_name ?: "Student"

                    val title = when (mentoring.status) {
                        "active" -> "Mentoring with $student ends"

                        "completed" -> "Mentoring with $student completed"

                        "expired" -> "Mentoring with $student expired"

                        else -> "Mentoring with $student"
                    }

                    val subtitle = if (mentoring.include_final_project == 1) {
                        "${mentoring.course_name} · Session ${mentoring.start_session} to Final project"
                    } else {
                        "${mentoring.course_name} · Sessions ${mentoring.start_session}-${mentoring.end_session}"
                    }

                    result.add(
                        MentorCalendarEvent(
                            date = date, title = title, subtitle = subtitle, type = mentoring.status
                        )
                    )
                }

                _events.value = result.sortedBy {
                    it.date
                }

            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to load calendar"
            }
        }
    }

    private fun parseDate(value: String?): LocalDate? {
        if (value.isNullOrBlank() || value.length < 10) {
            return null
        }

        return try {
            LocalDate.parse(value.take(10))
        } catch (_: Exception) {
            null
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}