package com.example.project.ui.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.project.R
import com.example.project.data.local.StudySessionDAO
import com.example.project.data.model.StudySession
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession
import java.util.Date

class QuizResultActivity : BaseActivity() {

    private val TAG = "QuizResult_DEBUG"

    private lateinit var tvResultTitle: TextView
    private lateinit var tvScoreBig: TextView
    private lateinit var tvCorrectCount: TextView
    private lateinit var tvWrongCount: TextView
    private lateinit var closeButton: Button

    private lateinit var studySessionDAO: StudySessionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)
        setHeaderTitle("Quiz Result\nTuan Kiet - Nhóm 2")
        setControl()
        setEvent()

//         setHeaderTitle("Kết quả Quiz")

        studySessionDAO = StudySessionDAO(this)

        val score = intent.getIntExtra("SCORE", -1)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", -1)

        Log.d(TAG, "Received Score: $score, Total: $totalQuestions")

        if (score == -1 || totalQuestions == -1) {
            setHeaderTitle("Lỗi Quiz")
            tvScoreBig.text = "Error"
            Log.e(TAG, "Error receiving quiz results from Intent.")
        } else {
            val correct = score
            val incorrect = totalQuestions - score

            tvScoreBig.text = "$score/$totalQuestions"
            tvCorrectCount.text = "$correct"
            tvWrongCount.text = "$incorrect"

            val percentage = if (totalQuestions > 0) (score.toFloat() / totalQuestions.toFloat()) * 100 else 0f
            if (percentage >= 80) {
                tvResultTitle.text = "Tuyệt vời! \uD83C\uDF89"
            } else if (percentage >= 50) {
                tvResultTitle.text = "Làm tốt lắm!"
            } else {
                tvResultTitle.text = "Cố gắng hơn nhé!"
            }

             saveQuizSession(totalQuestions)
        }
    }

    private fun setControl() {
        tvResultTitle = findViewById(R.id.tvTitle)
        tvScoreBig = findViewById(R.id.tvScoreBig)
        tvCorrectCount = findViewById(R.id.tvCorrectCount)
        tvWrongCount = findViewById(R.id.tvWrongCount)
        closeButton = findViewById(R.id.closeButton)
    }

    private fun setEvent() {
        closeButton.setOnClickListener { finish() }
    }

    private fun saveQuizSession(questionCount: Int) {
        val userId = UserSession.getUserId(this)
        if (userId != -1 && questionCount > 0) {
            val session = StudySession(
                userId = userId,
                wordsCount = questionCount,
                date = Date()
            )
            studySessionDAO.addSession(session)
            Log.d(TAG, "Quiz session saved for user $userId with $questionCount questions.")
        } else {
            Log.w(TAG, "Could not save quiz session. UserId: $userId, QuestionCount: $questionCount")
        }
    }
}
