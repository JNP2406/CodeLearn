package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient

class NotificationRepository {

    private val api = ApiClient.apiService

    suspend fun getNotifications(token: String) = api.getNotifications("Bearer $token")

    suspend fun getUnreadCount(token: String) = api.getNotificationUnreadCount("Bearer $token")

    suspend fun markAllRead(token: String) = api.markAllNotificationsRead("Bearer $token")

    suspend fun markRead(token: String, notificationId: Int) =
        api.markNotificationRead("Bearer $token", notificationId)
}