package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.AuthResponse
import com.example.codelearn.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> = _authResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(email: String, password: String, role: String, name: String, campus: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repository.register(
                    email = email, password = password, role = role, name = name, campus = campus
                )

                if (response.isSuccessful && response.body() != null) {
                    _authResult.value = Result.success(response.body()!!)
                } else {
                    _authResult.value =
                        Result.failure(Exception("Register gagal: ${response.code()}"))
                }

            } catch (e: Exception) {
                _authResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repository.login(email = email, password = password)

                if (response.isSuccessful && response.body() != null) {
                    _authResult.value = Result.success(response.body()!!)
                } else {
                    _authResult.value = Result.failure(Exception("Login gagal: ${response.code()}"))
                }

            } catch (e: Exception) {
                _authResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}