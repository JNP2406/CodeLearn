package com.example.codelearn

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.codelearn.data.remote.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvApiStatus = findViewById<TextView>(R.id.tvApiStatus)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.checkHealth()

                if (response.isSuccessful) {
                    tvApiStatus.text = response.body()?.message ?: "API connected"
                } else {
                    tvApiStatus.text = "API error: ${response.code()}"
                }

            } catch (e: Exception) {
                tvApiStatus.text = "Connection failed: ${e.message}"
            }
        }
    }
}