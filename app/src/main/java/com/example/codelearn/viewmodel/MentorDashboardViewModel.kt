package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentoringRepository
import com.example.codelearn.model.MentorDashboardResponse
import kotlinx.coroutines.launch

class MentorDashboardViewModel : ViewModel() {

    private val repository =
        MentoringRepository()

    private val _dashboard =
        MutableLiveData<MentorDashboardResponse>()

    val dashboard:
            LiveData<MentorDashboardResponse> =
        _dashboard

    private val _message =
        MutableLiveData<String?>()

    val message:
            LiveData<String?> =
        _message

    private val _loading =
        MutableLiveData(false)

    val loading:
            LiveData<Boolean> =
        _loading

    fun loadDashboard(
        token: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val response =
                    repository
                        .getMentorDashboard(token)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _dashboard.value = it
                    }
                } else {
                    _message.value =
                        "Failed to load mentoring data"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message
                        ?: "Failed to load mentoring data"
            } finally {
                _loading.value = false
            }
        }
    }

    fun respondRequest(
        token: String,
        requestId: Int,
        action: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val response =
                    repository.respondRequest(
                        token,
                        requestId,
                        action
                    )

                if (response.isSuccessful) {
                    _message.value =
                        response.body()?.message

                    loadDashboard(token)
                } else {
                    _message.value =
                        response.errorBody()
                            ?.string()
                            ?: "Failed to respond to request"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message
                        ?: "Failed to respond to request"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}