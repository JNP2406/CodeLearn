package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.remote.MaterialListResponse
import com.example.codelearn.data.repository.MaterialRepository
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {

    private val repository = MaterialRepository()

    private val _materials = MutableLiveData<Result<MaterialListResponse>>()

    val materials: LiveData<Result<MaterialListResponse>> = _materials

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> = _isLoading

    fun loadSessionMaterials(sessionId: Int) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response = repository.getSessionMaterials(sessionId)

                if (response.isSuccessful && response.body() != null) {

                    _materials.value = Result.success(response.body()!!)

                } else {

                    _materials.value =
                        Result.failure(Exception("Load materials failed: ${response.code()}"))
                }

            } catch (e: Exception) {

                _materials.value = Result.failure(e)

            } finally {

                _isLoading.value = false
            }
        }
    }
}