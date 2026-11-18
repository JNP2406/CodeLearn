package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.CaseStudyResponse
import com.example.codelearn.data.repository.CaseStudyRepository
import kotlinx.coroutines.launch

class CaseStudyViewModel : ViewModel() {

    private val repository = CaseStudyRepository()

    private val _caseStudy = MutableLiveData<Result<CaseStudyResponse>>()

    val caseStudy: LiveData<Result<CaseStudyResponse>> = _caseStudy

    fun loadCaseStudy(token: String, courseId: Int) {

        viewModelScope.launch {

            try {

                val response = repository.getCaseStudy(token, courseId)

                if (response.isSuccessful && response.body() != null) {

                    _caseStudy.value = Result.success(response.body()!!)

                } else {

                    _caseStudy.value =
                        Result.failure(Exception("Load case study failed: ${response.code()}"))
                }

            } catch (e: Exception) {

                _caseStudy.value = Result.failure(e)
            }
        }
    }
}