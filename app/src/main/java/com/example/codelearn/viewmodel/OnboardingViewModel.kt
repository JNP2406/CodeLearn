package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val _selectedRole = MutableLiveData<String?>()
    val selectedRole: LiveData<String?> = _selectedRole

    fun nextPage() {
        val page = _currentPage.value ?: 0
        if (page < 2) {
            _currentPage.value = page + 1
        }
    }

    fun previousPage() {
        val page = _currentPage.value ?: 0
        if (page > 0) {
            _currentPage.value = page - 1
        }
    }

    fun selectRole(role: String) {
        _selectedRole.value = role
    }
}

