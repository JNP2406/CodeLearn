package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.CertificateListResponse
import com.example.codelearn.data.remote.ClaimCertificateResponse
import com.example.codelearn.data.repository.CertificateRepository
import kotlinx.coroutines.launch

class CertificateViewModel : ViewModel() {

    private val repository = CertificateRepository()

    private val _claimResult = MutableLiveData<Result<ClaimCertificateResponse>>()

    val claimResult: LiveData<Result<ClaimCertificateResponse>> = _claimResult

    private val _certificates = MutableLiveData<Result<CertificateListResponse>>()

    val certificates: LiveData<Result<CertificateListResponse>> = _certificates

    fun claimCertificate(token: String, courseId: Int) {

        viewModelScope.launch {

            try {

                val response = repository.claimCertificate(token, courseId)

                if (response.isSuccessful && response.body() != null) {

                    _claimResult.value = Result.success(response.body()!!)

                } else {

                    _claimResult.value =
                        Result.failure(Exception("Claim certificate failed: ${response.code()}"))
                }

            } catch (e: Exception) {

                _claimResult.value = Result.failure(e)
            }
        }
    }

    fun loadMyCertificates(token: String) {

        viewModelScope.launch {

            try {

                val response = repository.getMyCertificates(token)

                if (response.isSuccessful && response.body() != null) {

                    _certificates.value = Result.success(response.body()!!)

                } else {

                    _certificates.value =
                        Result.failure(Exception("Load certificates failed: ${response.code()}"))
                }

            } catch (e: Exception) {

                _certificates.value = Result.failure(e)
            }
        }
    }
}