package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.CourseDetail
import com.example.codelearn.data.remote.CourseResponse
import com.example.codelearn.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel : ViewModel() {

    private val repository = CourseRepository()

    private val _courses = MutableLiveData<List<CourseResponse>>()
    val courses: LiveData<List<CourseResponse>> = _courses

    private val _courseDetail = MutableLiveData<CourseDetail?>()
    val courseDetail: LiveData<CourseDetail?> = _courseDetail

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getCourses()

                if (response.isSuccessful && response.body() != null) {
                    _courses.value = response.body()!!.courses
                } else {
                    _errorMessage.value = "Failed to load courses: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to connect to server"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCourseDetail(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getCourseDetail(courseId)

                if (response.isSuccessful && response.body() != null) {
                    _courseDetail.value = response.body()!!.course
                } else {
                    _errorMessage.value = "Failed to load course: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to connect to server"
            } finally {
                _isLoading.value = false
            }
        }
    }
}