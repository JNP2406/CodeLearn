package com.example.codelearn.ui.student

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.codelearn.R
import com.example.codelearn.data.local.SessionManager
import com.example.codelearn.data.remote.QuizAnswerRequest
import com.example.codelearn.data.remote.QuizQuestionResponse
import com.example.codelearn.data.remote.QuizReviewQuestionResponse
import com.example.codelearn.data.remote.QuizSubmitRequest
import com.example.codelearn.util.ActivityConfig
import com.example.codelearn.viewmodel.QuizViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class QuizActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    private lateinit var btnBackArea: LinearLayout

    private lateinit var tvQuizTitle: TextView

    private lateinit var tvSessionName: TextView

    private lateinit var tvQuizInfo: TextView

    private lateinit var questionContainer: LinearLayout

    private lateinit var tvAnsweredCount: TextView

    private lateinit var btnSubmitQuiz: MaterialButton

    private var sessionId = -1

    private var sessionNumber = 1

    private var courseName = ""

    private var reviewMode = false

    private var questions: List<QuizQuestionResponse> = emptyList()

    private val selectedAnswers = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz)
        ActivityConfig.config(window)

        sessionManager = SessionManager(this)

        sessionId = intent.getIntExtra("SESSION_ID", -1)

        sessionNumber = intent.getIntExtra("SESSION_NUMBER", 1)

        courseName = intent.getStringExtra("COURSE_NAME") ?: ""

        reviewMode = intent.getBooleanExtra("REVIEW_MODE", false)

        if (sessionId == -1) {

            Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show()

            finish()
            return
        }

        bindViews()
        setupListeners()
        setupObservers()

        tvQuizTitle.text = if (reviewMode) {
            "Session Test Review"
        } else {
            "Session Test"
        }

        tvSessionName.text = "$courseName · Session $sessionNumber"

        if (reviewMode) {
            loadQuizReview()
        } else {
            loadQuiz()
        }
    }

    private fun bindViews() {

        btnBackArea = findViewById(R.id.btnBackArea)

        tvQuizTitle = findViewById(R.id.tvQuizTitle)

        tvSessionName = findViewById(R.id.tvSessionName)

        tvQuizInfo = findViewById(R.id.tvQuizInfo)

        questionContainer = findViewById(R.id.questionContainer)

        tvAnsweredCount = findViewById(R.id.tvAnsweredCount)

        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz)
    }

    private fun setupListeners() {

        btnBackArea.setOnClickListener {

            finish()
        }

        btnSubmitQuiz.setOnClickListener {

            if (reviewMode) {
                return@setOnClickListener
            }

            if (questions.isEmpty()) {
                return@setOnClickListener
            }

            if (selectedAnswers.size != questions.size) {

                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            showSubmitConfirmation()
        }
    }

    private fun setupObservers() {

        quizViewModel.quiz.observe(this) { result ->

            result.onSuccess { response ->

                questions = response.questions

                tvSessionName.text = "$courseName · Session ${response.session.session_number}"

                tvQuizInfo.text =
                    "${response.total_questions} questions · Minimum 8 correct to pass"

                showQuestions(response.questions)

                updateAnsweredCounter()
            }

            result.onFailure { error ->

                Toast.makeText(
                    this, error.message ?: "Failed to load quiz", Toast.LENGTH_LONG
                ).show()
            }
        }

        quizViewModel.quizResult.observe(this) { result ->

            result.onSuccess { response ->

                tvQuizTitle.text = "Session Test Review"

                tvSessionName.text = "$courseName · Session ${response.session.session_number}"

                tvQuizInfo.text =
                    "Score ${response.score} · ${response.correct_answers}/${response.total_questions} correct"

                questionContainer.removeAllViews()

                btnSubmitQuiz.visibility = View.GONE

                tvAnsweredCount.text = "Completed"

                response.questions.forEachIndexed { index, question ->

                    addReviewQuestionCard(index + 1, question)
                }
            }

            result.onFailure { error ->

                Toast.makeText(
                    this, error.message ?: "Failed to load result", Toast.LENGTH_LONG
                ).show()
            }
        }

        quizViewModel.submitResult.observe(this) { result ->

            result.onSuccess { response ->

                if (response.passed) {

                    val intent = Intent(this, QuizResultActivity::class.java)

                    intent.putExtra("SCORE", response.score)

                    intent.putExtra("CORRECT", response.correct_answers)

                    intent.putExtra("TOTAL", response.total_questions)

                    intent.putExtra("NEXT_UNLOCKED", response.next_session_unlocked)

                    startActivity(intent)

                    finish()

                } else {

                    btnSubmitQuiz.text = "Submit Test"

                    btnSubmitQuiz.isEnabled = true

                    AlertDialog.Builder(this)
                        .setTitle("Keep Trying")
                        .setMessage("Score: ${response.score}\n" + "${response.correct_answers} of ${response.total_questions} correct\n\n" + "You need at least 8 correct answers to pass.")
                        .setPositiveButton("Try Again", null)
                        .show()
                }
            }

            result.onFailure { error ->

                btnSubmitQuiz.isEnabled = true

                btnSubmitQuiz.text = "Submit Test"

                Toast.makeText(
                    this, error.message ?: "Failed to submit quiz", Toast.LENGTH_LONG
                ).show()
            }
        }

        quizViewModel.isLoading.observe(this) { loading ->

            if (loading && !reviewMode) {

                btnSubmitQuiz.isEnabled = false

            } else if (!reviewMode && questions.isNotEmpty()) {

                updateAnsweredCounter()
            }
        }
    }

    private fun loadQuiz() {

        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {

            Toast.makeText(this, "Login session not found", Toast.LENGTH_LONG).show()

            finish()
            return
        }

        questionContainer.removeAllViews()

        val loadingText = TextView(this)

        loadingText.text = "Loading quiz..."

        loadingText.textSize = 14f

        loadingText.setTextColor(Color.parseColor("#64748B"))

        questionContainer.addView(loadingText)

        quizViewModel.loadQuiz(token = token, sessionId = sessionId)
    }

    private fun loadQuizReview() {

        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {

            Toast.makeText(this, "Login session not found", Toast.LENGTH_LONG).show()

            finish()
            return
        }

        questionContainer.removeAllViews()

        val loadingText = TextView(this)

        loadingText.text = "Loading result..."

        loadingText.textSize = 14f

        loadingText.setTextColor(Color.parseColor("#64748B"))

        questionContainer.addView(loadingText)

        btnSubmitQuiz.visibility = View.GONE

        quizViewModel.loadQuizResult(token, sessionId)
    }

    private fun showQuestions(questionList: List<QuizQuestionResponse>) {

        questionContainer.removeAllViews()

        selectedAnswers.clear()

        questionList.forEachIndexed { index, question ->

            addQuestionCard(index + 1, question)
        }
    }

    private fun addQuestionCard(number: Int, question: QuizQuestionResponse) {

        val card = MaterialCardView(this)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cardParams.bottomMargin = dpToPx(14)

        card.layoutParams = cardParams

        card.radius = dpToPx(12).toFloat()

        card.cardElevation = 0f

        card.strokeWidth = dpToPx(1)

        card.strokeColor = Color.parseColor("#D5DCE6")

        card.setCardBackgroundColor(Color.WHITE)

        val content = LinearLayout(this)

        content.orientation = LinearLayout.VERTICAL

        content.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        val numberText = TextView(this)

        numberText.text = "Question $number"

        numberText.textSize = 12f

        numberText.setTextColor(Color.parseColor("#2F6FED"))

        numberText.setTypeface(numberText.typeface, Typeface.BOLD)

        val questionText = TextView(this)

        questionText.text = question.question

        questionText.textSize = 16f

        questionText.setTextColor(Color.parseColor("#0F172A"))

        questionText.setTypeface(questionText.typeface, Typeface.BOLD)

        questionText.setPadding(0, dpToPx(6), 0, dpToPx(10))

        val radioGroup = RadioGroup(this)

        radioGroup.orientation = RadioGroup.VERTICAL

        question.options.forEach { option ->

            val radio = RadioButton(this)

            radio.id = View.generateViewId()

            radio.text = option.option_text

            radio.textSize = 14f

            radio.setTextColor(Color.parseColor("#334155"))

            radio.setPadding(0, dpToPx(5), 0, dpToPx(5))

            radio.tag = option.id

            radioGroup.addView(radio)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            val selectedRadio = group.findViewById<RadioButton>(checkedId)

            val optionId = selectedRadio.tag as Int

            selectedAnswers[question.id] = optionId

            updateAnsweredCounter()
        }

        content.addView(numberText)

        content.addView(questionText)

        content.addView(radioGroup)

        card.addView(content)

        questionContainer.addView(card)
    }

    private fun addReviewQuestionCard(number: Int, question: QuizReviewQuestionResponse) {

        val card = MaterialCardView(this)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cardParams.bottomMargin = dpToPx(14)

        card.layoutParams = cardParams

        card.radius = dpToPx(12).toFloat()

        card.cardElevation = 0f

        card.strokeWidth = dpToPx(1)

        card.strokeColor = Color.parseColor("#D5DCE6")

        card.setCardBackgroundColor(Color.WHITE)

        val content = LinearLayout(this)

        content.orientation = LinearLayout.VERTICAL

        content.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        val numberText = TextView(this)

        numberText.text = "Question $number"

        numberText.textSize = 12f

        numberText.setTextColor(Color.parseColor("#2F6FED"))

        numberText.setTypeface(numberText.typeface, Typeface.BOLD)

        val questionText = TextView(this)

        questionText.text = question.question

        questionText.textSize = 16f

        questionText.setTextColor(Color.parseColor("#0F172A"))

        questionText.setTypeface(questionText.typeface, Typeface.BOLD)

        questionText.setPadding(0, dpToPx(6), 0, dpToPx(10))

        content.addView(numberText)

        content.addView(questionText)

        question.options.forEach { option ->

            val optionText = TextView(this)

            val selected = option.id == question.selected_option_id

            val correct = option.id == question.correct_option_id

            optionText.text = when {

                selected && correct -> "✓ ${option.option_text}"

                selected -> "✕ ${option.option_text}"

                correct -> "✓ ${option.option_text}"

                else -> option.option_text
            }

            optionText.textSize = 14f

            optionText.setPadding(dpToPx(8), dpToPx(7), dpToPx(8), dpToPx(7))

            optionText.setTextColor(
                when {

                    correct -> Color.parseColor("#15803D")

                    selected -> Color.parseColor("#DC2626")

                    else -> Color.parseColor("#64748B")
                }
            )

            content.addView(optionText)
        }

        card.addView(content)

        questionContainer.addView(card)
    }

    private fun updateAnsweredCounter() {

        val total = questions.size

        val answered = selectedAnswers.size

        tvAnsweredCount.text = "$answered of $total answered"

        btnSubmitQuiz.isEnabled = (total > 0 && answered == total)
    }

    private fun showSubmitConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Submit test?")
            .setMessage("Make sure you have answered all questions before submitting.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Submit") { _, _ ->

                submitQuiz()
            }
            .show()
    }

    private fun submitQuiz() {

        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            return
        }

        val answers = questions.map { question ->

            QuizAnswerRequest(question_id = question.id, option_id = selectedAnswers[question.id]!!)
        }

        val request = QuizSubmitRequest(answers = answers)

        btnSubmitQuiz.isEnabled = false

        btnSubmitQuiz.text = "Submitting..."

        quizViewModel.submitQuiz(token = token, sessionId = sessionId, request = request)
    }

    private fun dpToPx(dp: Int): Int {

        return (dp * resources.displayMetrics.density).toInt()
    }
}