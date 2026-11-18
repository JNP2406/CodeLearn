package com.example.codelearn.model

data class NotificationListResponse(val notifications: List<NotificationItem>)

data class NotificationItem(
    val id: Int,
    val user_id: Int,
    val type: String,
    val title: String,
    val message: String,
    val is_read: Int,
    val created_at: String?
)

data class NotificationUnreadCountResponse(val unread_count: Int)

data class NotificationActionResponse(val message: String)