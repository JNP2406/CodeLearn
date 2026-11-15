package com.example.codelearn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelearn.data.repository.NotificationRepository
import com.example.codelearn.model.NotificationItem
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val repository =
        NotificationRepository()

    private val _notifications =
        MutableLiveData<List<NotificationItem>>()

    val notifications:
            LiveData<List<NotificationItem>> =
        _notifications

    private val _unreadCount =
        MutableLiveData<Int>()

    val unreadCount:
            LiveData<Int> =
        _unreadCount

    private val _message =
        MutableLiveData<String?>()

    val message:
            LiveData<String?> =
        _message

    fun loadNotifications(
        token: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getNotifications(
                        token
                    )

                if (response.isSuccessful) {
                    _notifications.value =
                        response.body()
                            ?.notifications
                            ?: emptyList()
                } else {
                    _message.value =
                        "Failed to load notifications"
                }

            } catch (
                e: Exception
            ) {
                _message.value =
                    e.message
                        ?: "Failed to load notifications"
            }
        }
    }

    fun loadUnreadCount(
        token: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getUnreadCount(
                        token
                    )

                if (response.isSuccessful) {
                    _unreadCount.value =
                        response.body()
                            ?.unread_count
                            ?: 0
                }

            } catch (_: Exception) {
            }
        }
    }

    fun markAllRead(
        token: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.markAllRead(
                        token
                    )

                if (response.isSuccessful) {
                    _unreadCount.value =
                        0
                }

            } catch (_: Exception) {
            }
        }
    }

    fun clearMessage() {
        _message.value =
            null
    }
}