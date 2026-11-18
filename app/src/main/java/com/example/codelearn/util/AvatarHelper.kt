package com.example.codelearn.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.codelearn.data.remote.ApiClient

object AvatarHelper {

    fun show(imageView: ImageView, initialView: TextView, imagePath: String?, name: String?) {
        initialView.text = getInitial(name)

        if (imagePath.isNullOrBlank()) {
            showInitial(imageView, initialView)

            return
        }

        val imageUrl = if (imagePath.startsWith("http")) {
            imagePath
        } else {
            "${ApiClient.BASE_URL}$imagePath"
        }

        imageView.visibility = View.VISIBLE

        initialView.visibility = View.GONE

        imageView.load(imageUrl) {
            crossfade(true)

            listener(onError = { _, _ ->
                showInitial(imageView, initialView)
            })
        }
    }

    private fun showInitial(imageView: ImageView, initialView: TextView) {
        imageView.visibility = View.GONE

        initialView.visibility = View.VISIBLE
    }

    private fun getInitial(name: String?): String {
        return name?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }
}