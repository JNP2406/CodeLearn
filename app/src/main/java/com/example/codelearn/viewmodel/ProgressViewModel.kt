package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.CourseProgressResponse
import com.example.codelearn.data.remote.TakeCourseResponse
import com.example.codelearn.data.repository.ProgressRepository
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val repository = ProgressRepository()

    private val _takeCourseResult =
        MutableLiveData<Result<TakeCourseResponse>>()

    val takeCourseResult:
            LiveData<Result<TakeCourseResponse>> =
        _takeCourseResult


    private val _courseProgress =
        MutableLiveData<Result<CourseProgressResponse>>()

    val courseProgress:
            LiveData<Result<CourseProgressResponse>> =
        _courseProgress


    private val _isLoading =
        MutableLiveData(false)

    val isLoading:
            LiveData<Boolean> =
        _isLoading


    fun takeCourse(
        token: String,
        courseId: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.takeCourse(
                        token = token,
                        courseId = courseId
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    _takeCourseResult.value =
                        Result.success(
                            response.body()!!
                        )

                } else {

                    _takeCourseResult.value =
                        Result.failure(
                            Exception(
                                "Take course failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _takeCourseResult.value =
                    Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }


    fun loadCourseProgress(
        token: String,
        courseId: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.getCourseProgress(
                        token = token,
                        courseId = courseId
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    _courseProgress.value =
                        Result.success(
                            response.body()!!
                        )

                } else {

                    _courseProgress.value =
                        Result.failure(
                            Exception(
                                "Load progress failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _courseProgress.value =
                    Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }
}