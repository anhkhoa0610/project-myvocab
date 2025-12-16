package com.example.project.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.project.R
import com.example.project.data.local.QuizDAO
import com.example.project.data.model.QuizQuestion
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession
import com.google.android.material.button.MaterialButton

class QuizActivity : BaseActivity() {

    private val TAG = "QuizActivity_DEBUG"

    // Views
    private lateinit var questionTextView: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var submitButton: MaterialButton
    private lateinit var btnFinishSubmit: TextView

    // Data
    private lateinit var quizDAO: QuizDAO
    private var currentQuestionIndex = 0
    private var score = 0
    private var questions: List<QuizQuestion> = emptyList()
    private var quizId: Int = -1
    private var userId: Int = -1
    private var userAnswers: MutableList<String?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        setControl()
        initDAOs()
        loadQuizData()
        setEvent()
    }

    private fun setControl() {
        questionTextView = findViewById(R.id.questionTextView)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.quizProgressBar)
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup)
        submitButton = findViewById(R.id.submitButton)
        btnFinishSubmit = findViewById(R.id.btnFinishSubmit)
    }

    private fun setEvent() {
        submitButton.setOnClickListener {
            handleNextOrSubmit()
        }

        btnFinishSubmit.setOnClickListener {
            Log.d(TAG, "'Finish Anytime' clicked.")
            saveCurrentSelection() // Lưu lựa chọn hiện tại trước khi thoát
            calculateAndFinish()
        }
    }

    private fun initDAOs() {
        quizDAO = QuizDAO(this)
    }

    private fun loadQuizData() {
        quizId = intent.getIntExtra("QUIZ_ID", -1)
        userId = UserSession.getUserId(this)

        if (quizId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID bài kiểm tra!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        questions = quizDAO.getQuestionsForQuiz(quizId)
        if (questions.isEmpty()) {
            Toast.makeText(this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        progressBar.max = questions.size
        userAnswers = MutableList(questions.size) { null }
        displayQuestion()
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]

            questionTextView.text = question.question
            tvDifficulty.text = question.difficulty.toString()

            val displayIndex = currentQuestionIndex + 1
            tvProgress.text = "$displayIndex/${questions.size}"
            progressBar.progress = displayIndex

            optionsRadioGroup.removeAllViews()
            optionsRadioGroup.clearCheck()

            question.options.forEach { option ->
                val radioButton = RadioButton(this).apply {
                    text = option
                    id = View.generateViewId()
                    textSize = 16f
                    setTextColor(Color.parseColor("#333333"))
                    buttonDrawable = null
                    background = ContextCompat.getDrawable(context, R.drawable.selector_quiz_option)

                    setPadding(50, 40, 50, 40)
                    val params = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 0, 0, 24)
                    layoutParams = params
                }
                optionsRadioGroup.addView(radioButton)
            }

            userAnswers[currentQuestionIndex]?.let { selected ->
                (0 until optionsRadioGroup.childCount).forEach { i ->
                    val rb = optionsRadioGroup.getChildAt(i) as RadioButton
                    if (rb.text == selected) rb.isChecked = true
                }
            }

            submitButton.text = if (currentQuestionIndex == questions.size - 1) "HOÀN THÀNH" else "CÂU TIẾP THEO"
        } else {
            calculateAndFinish()
        }
    }

    private fun saveCurrentSelection() {
        val selectedId = optionsRadioGroup.checkedRadioButtonId
        if (selectedId != -1) {
            val selectedRb = findViewById<RadioButton>(selectedId)
            userAnswers[currentQuestionIndex] = selectedRb.text.toString()
        }
    }

    private fun handleNextOrSubmit() {
        val selectedId = optionsRadioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn một đáp án!", Toast.LENGTH_SHORT).show()
            return
        }

        saveCurrentSelection()

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayQuestion()
        } else {
            calculateAndFinish()
        }
    }

    private fun calculateAndFinish() {
        score = 0
        for (i in questions.indices) {
            val userAnswer = userAnswers.getOrNull(i)
            val correctAnswer = questions[i].answer
            if (userAnswer == correctAnswer) {
                score++
            }
        }

        if (userId != -1) {
            quizDAO.saveQuizResult(quizId, userId, score)
        }

        val intent = Intent(this, QuizResultActivity::class.java).apply {
            putExtra("SCORE", score)
            putExtra("TOTAL_QUESTIONS", questions.size)
        }
        startActivity(intent)
        finish()
    }
}
