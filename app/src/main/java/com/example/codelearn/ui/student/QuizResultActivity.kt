package com.example.codelearn.ui.student

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.util.ActivityConfig
import com.google.android.material.button.MaterialButton

class QuizResultActivity : AppCompatActivity() {

    private lateinit var btnBackArea: LinearLayout

    private lateinit var tvScore: TextView

    private lateinit var tvCorrect: TextView

    private lateinit var tvResultMessage: TextView

    private lateinit var btnContinue: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz_result)
        ActivityConfig.config(window)

        btnBackArea = findViewById(R.id.btnBackArea)

        tvScore = findViewById(R.id.tvScore)

        tvCorrect = findViewById(R.id.tvCorrect)

        tvResultMessage = findViewById(R.id.tvResultMessage)

        btnContinue = findViewById(R.id.btnContinue)

        val score = intent.getIntExtra("SCORE", 0)

        val correct = intent.getIntExtra("CORRECT", 0)

        val total = intent.getIntExtra("TOTAL", 10)

        val nextUnlocked = intent.getBooleanExtra("NEXT_UNLOCKED", false)

        tvScore.text = score.toString()

        tvCorrect.text = "$correct of $total correct"

        tvResultMessage.text = if (nextUnlocked) {
            "Great job! The next session is now unlocked."
        } else {
            "Great job! You completed this session."
        }

        btnBackArea.setOnClickListener {
            finish()
        }

        btnContinue.setOnClickListener {
            finish()
        }
    }
}