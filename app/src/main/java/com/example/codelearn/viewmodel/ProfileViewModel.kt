package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.StudentCertificateResponse
import com.example.codelearn.data.repository.ProfileRepository
import com.example.codelearn.model.ProfileSpecialization
import com.example.codelearn.model.ProfileUser
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profile = MutableLiveData<ProfileUser>()
    val profile: LiveData<ProfileUser> = _profile

    private val _specializations =
        MutableLiveData<List<ProfileSpecialization>>(emptyList())

    val specializations: LiveData<List<ProfileSpecialization>> =
        _specializations

    private val _certificates =
        MutableLiveData<List<StudentCertificateResponse>>(emptyList())

    val certificates: LiveData<List<StudentCertificateResponse>> =
        _certificates

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val response =
                    repository.getProfile(token)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        _profile.value = body.user
                        _specializations.value =
                            body.specializations
                    }
                } else {
                    _message.value =
                        "Failed to load profile"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadCertificates(token: String) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getCertificates(token)

                if (response.isSuccessful) {
                    _certificates.value =
                        response.body()?.certificates
                            ?: emptyList()
                }
            } catch (_: Exception) {
            }
        }
    }

    fun updateProfile(
        token: String,
        name: String,
        campus: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val response =
                    repository.updateProfile(
                        token,
                        name,
                        campus
                    )

                if (response.isSuccessful) {
                    _message.value =
                        "Profile updated"

                    loadProfile(token)
                } else {
                    _message.value =
                        "Failed to update profile"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message ?: "Failed to update profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateSpecializations(
        token: String,
        courseIds: List<Int>
    ) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val response =
                    repository.updateSpecializations(
                        token,
                        courseIds
                    )

                if (response.isSuccessful) {
                    _message.value =
                        "Specializations updated"

                    loadProfile(token)
                } else {
                    _message.value =
                        "Failed to update specializations"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message
                        ?: "Failed to update specializations"
            } finally {
                _loading.value = false
            }
        }
    }

    fun uploadProfileImage(
        token: String,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.uploadProfileImage(
                        token,
                        image
                    )

                if (response.isSuccessful) {
                    _message.value =
                        "Profile photo updated"

                    loadProfile(token)
                } else {
                    _message.value =
                        "Failed to upload photo"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message ?: "Failed to upload photo"
            }
        }
    }

    fun uploadCoverImage(
        token: String,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.uploadCoverImage(
                        token,
                        image
                    )

                if (response.isSuccessful) {
                    _message.value =
                        "Cover updated"

                    loadProfile(token)
                } else {
                    _message.value =
                        "Failed to upload cover"
                }
            } catch (e: Exception) {
                _message.value =
                    e.message ?: "Failed to upload cover"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}