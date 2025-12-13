package com.example.project.ui.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.QuizDAO
import com.example.project.data.model.QuizQuestion
import com.example.project.utils.UserSession

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var submitButton: Button

    private lateinit var quizDAO: QuizDAO
    private var currentQuestionIndex = 0
    private var score = 0
    private var questions: List<QuizQuestion> = emptyList()
    private var quizId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        initViews()
        initDAOs()
        loadQuizData()

        submitButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun initViews() {
        questionTextView = findViewById(R.id.questionTextView)
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup)
        submitButton = findViewById(R.id.submitButton)
    }

    private fun initDAOs() {
        // Khởi tạo DAO với Context (this)
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
            Toast.makeText(this, "Không có câu hỏi nào cho bài kiểm tra này!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayQuestion()
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionTextView.text = question.question

            optionsRadioGroup.removeAllViews()
            optionsRadioGroup.clearCheck()

            question.options.forEach { option ->
                val radioButton = RadioButton(this).apply {
                    text = option
                    id = android.view.View.generateViewId()
                }
                optionsRadioGroup.addView(radioButton)
            }
        } else {
            finishQuiz()
        }
    }

    private fun checkAnswer() {
        val selectedRadioButtonId = optionsRadioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Vui lòng chọn một đáp án!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val selectedAnswer = selectedRadioButton.text.toString()

        if (selectedAnswer == questions[currentQuestionIndex].answer) {
            score++
            Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sai rồi! Đáp án là: ${questions[currentQuestionIndex].answer}", Toast.LENGTH_SHORT).show()
        }

        currentQuestionIndex++
        optionsRadioGroup.postDelayed({ displayQuestion() }, 1000)
    }

    private fun finishQuiz() {
        if (userId != -1) {
            quizDAO.saveQuizResult(quizId, userId, score)
        }

        val message = "Hoàn thành! Điểm của bạn: $score/${questions.size}"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}
