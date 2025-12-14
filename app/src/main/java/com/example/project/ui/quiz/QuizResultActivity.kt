package com.example.project.ui.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.project.R
import com.example.project.ui.base.BaseActivity

class QuizResultActivity : BaseActivity() {

    private val TAG = "QuizResult_DEBUG"

    // Khai báo các view mới theo layout
    private lateinit var tvScoreBig: TextView
    private lateinit var tvCorrectCount: TextView
    private lateinit var tvWrongCount: TextView
//    private lateinit var tvTitle: TextView
    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        // Ánh xạ View
        tvScoreBig = findViewById(R.id.tvScoreBig)
        tvCorrectCount = findViewById(R.id.tvCorrectCount)
        tvWrongCount = findViewById(R.id.tvWrongCount)
        tvTitle = findViewById(R.id.tvTitle)
        closeButton = findViewById(R.id.closeButton)

        // Lấy dữ liệu từ Intent (Logic cũ giữ nguyên)
        val score = intent.getIntExtra("SCORE", -1)
        val total = intent.getIntExtra("TOTAL_QUESTIONS", -1)

        Log.d(TAG, "Received Score: $score, Total: $total")

        if (score == -1 || total == -1) {
            tvTitle.text = "Lỗi!"
            tvScoreBig.text = "Error"
            Log.e(TAG, "Error receiving quiz results from Intent.")
        } else {
            val correct = score
            val incorrect = total - score

            // Hiển thị dữ liệu lên giao diện mới
            tvScoreBig.text = "$score/$total"
            tvCorrectCount.text = "$correct"
            tvWrongCount.text = "$incorrect"

            // Thêm chút logic vui vẻ: Đổi tiêu đề dựa trên điểm số
            val percentage = (score.toFloat() / total.toFloat()) * 100
            if (percentage >= 80) {
                tvTitle.text = "Tuyệt vời! \uD83C\uDF89" // Icon pháo hoa
            } else if (percentage >= 50) {
                tvTitle.text = "Làm tốt lắm!"
            } else {
                tvTitle.text = "Cố gắng hơn nhé!"
            }
        }

        closeButton.setOnClickListener { finish() }
    }
}