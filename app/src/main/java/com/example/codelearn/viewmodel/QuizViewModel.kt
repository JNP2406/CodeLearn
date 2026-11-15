package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.QuizResponse
import com.example.codelearn.data.remote.QuizResultResponse
import com.example.codelearn.data.remote.QuizSubmitRequest
import com.example.codelearn.data.remote.QuizSubmitResponse
import com.example.codelearn.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val repository =
        QuizRepository()

    private val _quiz =
        MutableLiveData<Result<QuizResponse>>()

    val quiz:
            LiveData<Result<QuizResponse>> =
        _quiz

    private val _quizResult =
        MutableLiveData<Result<QuizResultResponse>>()

    val quizResult:
            LiveData<Result<QuizResultResponse>> =
        _quizResult

    private val _submitResult =
        MutableLiveData<Result<QuizSubmitResponse>>()

    val submitResult:
            LiveData<Result<QuizSubmitResponse>> =
        _submitResult

    private val _isLoading =
        MutableLiveData<Boolean>()

    val isLoading:
            LiveData<Boolean> =
        _isLoading

    fun loadQuiz(
        token: String,
        sessionId: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.getQuiz(
                        token,
                        sessionId
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    _quiz.value =
                        Result.success(
                            response.body()!!
                        )

                } else {

                    _quiz.value =
                        Result.failure(
                            Exception(
                                "Load quiz failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _quiz.value =
                    Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun loadQuizResult(
        token: String,
        sessionId: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.getQuizResult(
                        token,
                        sessionId
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    _quizResult.value =
                        Result.success(
                            response.body()!!
                        )

                } else {

                    _quizResult.value =
                        Result.failure(
                            Exception(
                                "Load quiz result failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _quizResult.value =
                    Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun submitQuiz(
        token: String,
        sessionId: Int,
        request: QuizSubmitRequest
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.submitQuiz(
                        token,
                        sessionId,
                        request
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    _submitResult.value =
                        Result.success(
                            response.body()!!
                        )

                } else {

                    _submitResult.value =
                        Result.failure(
                            Exception(
                                "Submit quiz failed: ${response.code()}"
                            )
                        )
                }

            } catch (e: Exception) {

                _submitResult.value =
                    Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }
}