package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentoringRepository
import com.example.codelearn.model.MentorListItem
import kotlinx.coroutines.launch

class FindMentorViewModel : ViewModel() {

    private val repository = MentoringRepository()

    private val _mentors = MutableLiveData<List<MentorListItem>>(emptyList())

    val mentors: LiveData<List<MentorListItem>> = _mentors

    private val _message = MutableLiveData<String?>()

    val message: LiveData<String?> = _message

    private val _requestSuccess = MutableLiveData(false)

    val requestSuccess: LiveData<Boolean> = _requestSuccess

    fun loadMentors(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getAvailableMentors(token)

                if (response.isSuccessful) {
                    _mentors.value = response.body()?.mentors ?: emptyList()
                } else {
                    _message.value = "Failed to load mentors"
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to load mentors"
            }
        }
    }

    fun sendRequest(
        token: String,
        mentorId: Int,
        courseId: Int,
        fromSession: Int,
        toSession: Int,
        includeFinalProject: Boolean
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createRequest(
                    token, mentorId, courseId, fromSession, toSession, includeFinalProject
                )

                if (response.isSuccessful) {
                    _message.value = response.body()?.message ?: "Mentoring request sent"

                    _requestSuccess.value = true
                } else {
                    _message.value = response.errorBody()?.string() ?: "Failed to send request"
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to send request"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun clearRequestSuccess() {
        _requestSuccess.value = false
    }
}