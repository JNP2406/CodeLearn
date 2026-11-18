package com.example.codelearn.model

import java.time.LocalDate

data class StudentCalendarEvent(
    val date: LocalDate, val title: String, val subtitle: String, val type: String
)