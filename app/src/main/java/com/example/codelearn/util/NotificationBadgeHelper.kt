package com.example.codelearn.util

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.repository.NotificationRepository
import com.example.codelearn.ui.NotificationActivity
import kotlinx.coroutines.launch

object NotificationBadgeHelper {

    fun setup(
        activity: AppCompatActivity,
        sessionManager: SessionManager,
        containerId: Int,
        badgeId: Int
    ) {
        activity.findViewById<View>(
            containerId
        ).setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    NotificationActivity::class.java
                )
            )
        }

        refresh(
            activity,
            sessionManager,
            badgeId
        )
    }

    fun refresh(
        activity: AppCompatActivity,
        sessionManager: SessionManager,
        badgeId: Int
    ) {
        val badge =
            activity.findViewById<TextView>(
                badgeId
            )

        activity.lifecycleScope.launch {
            try {
                val token =
                    sessionManager.getToken()
                        ?: return@launch

                val response =
                    NotificationRepository()
                        .getUnreadCount(
                            token
                        )

                val count =
                    response.body()
                        ?.unread_count
                        ?: 0

                if (
                    response.isSuccessful &&
                    count > 0
                ) {
                    badge.visibility =
                        View.VISIBLE

                    badge.text =
                        if (
                            count > 99
                        ) {
                            "99+"
                        } else {
                            count.toString()
                        }

                } else {
                    badge.visibility =
                        View.GONE
                }

            } catch (_: Exception) {
                badge.visibility =
                    View.GONE
            }
        }
    }
}