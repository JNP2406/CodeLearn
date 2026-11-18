package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentoringRepository
import com.example.codelearn.model.StudentMentoringItem
import kotlinx.coroutines.launch

class StudentMentorViewModel : ViewModel() {

    private val repository = MentoringRepository()

    private val _mentorings = MutableLiveData<List<StudentMentoringItem>>(emptyList())

    val mentorings: LiveData<List<StudentMentoringItem>> = _mentorings

    private val _message = MutableLiveData<String?>()

    val message: LiveData<String?> = _message

    fun loadMentorings(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStudentMentorings(token)

                if (response.isSuccessful) {
                    _mentorings.value = response.body()?.mentorings ?: emptyList()
                } else {
                    _message.value = "Failed to load mentorings"
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to load mentorings"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}