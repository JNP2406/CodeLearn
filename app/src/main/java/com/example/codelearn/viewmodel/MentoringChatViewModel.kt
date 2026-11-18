package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.MentoringRepository
import com.example.codelearn.model.MentoringStatusResponse
import kotlinx.coroutines.launch

class MentoringChatViewModel : ViewModel() {

    private val repository = MentoringRepository()

    private val _status = MutableLiveData<MentoringStatusResponse>()

    val status: LiveData<MentoringStatusResponse> = _status

    private val _message = MutableLiveData<String?>()

    val message: LiveData<String?> = _message

    fun loadStatus(token: String, mentoringId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMentoringStatus(token, mentoringId)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        _status.value = body
                    }
                } else {
                    _message.value = "Failed to load mentoring status"
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to load mentoring status"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}