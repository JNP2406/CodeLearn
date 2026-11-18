package com.example.codelearn.util

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object ActivityConfig {
  fun config(window: Window) {
    val windowInsetsControllerCompat = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars())
    windowInsetsControllerCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  }
}