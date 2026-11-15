package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentorCaseStudyRepository
import com.example.codelearn.model.MentorCaseStudyResponse
import kotlinx.coroutines.launch

class MentorCaseStudyViewModel : ViewModel() {

    private val repository =
        MentorCaseStudyRepository()

    private val _data =
        MutableLiveData<MentorCaseStudyResponse>()

    val data:
            LiveData<MentorCaseStudyResponse> =
        _data

    private val _message =
        MutableLiveData<String?>()

    val message:
            LiveData<String?> =
        _message

    private val _reviewFinished =
        MutableLiveData(false)

    val reviewFinished:
            LiveData<Boolean> =
        _reviewFinished

    fun loadCaseStudy(
        token: String,
        mentoringId: Int
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getCaseStudy(
                        token,
                        mentoringId
                    )

                if (response.isSuccessful) {
                    response.body()?.let {
                        _data.value = it
                    }
                } else {
                    _message.value =
                        response.errorBody()
                            ?.string()
                            ?: "Failed to load case study"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message
                        ?: "Failed to load case study"
            }
        }
    }

    fun submitReview(
        token: String,
        reviewId: Int,
        status: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.submitReview(
                        token,
                        reviewId,
                        status
                    )

                if (response.isSuccessful) {
                    _message.value =
                        response.body()?.message

                    _reviewFinished.value =
                        true
                } else {
                    _message.value =
                        response.errorBody()
                            ?.string()
                            ?: "Failed to submit review"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message
                        ?: "Failed to submit review"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}