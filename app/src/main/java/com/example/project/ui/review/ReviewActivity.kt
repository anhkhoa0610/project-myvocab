package com.example.project.ui.review

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.local.StudySessionDAO
import com.example.project.data.local.UserWordStatusDAO
import com.example.project.data.model.DictionaryWord
import com.example.project.data.model.StudySession
import com.example.project.data.model.UserWordStatus
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession
import com.google.android.material.button.MaterialButton
import java.util.Date

class ReviewActivity : BaseActivity() {

    // Data
    private lateinit var wordsToReview: List<DictionaryWord>
    private var currentWordIndex = 0
    private var reviewedWordsCount = 0
    private var userId: Int = -1

    // DAOs
    private lateinit var dictionaryWordDAO: DictionaryWordDAO
    private lateinit var userWordStatusDAO: UserWordStatusDAO
    private lateinit var studySessionDAO: StudySessionDAO

    // Views
    private lateinit var wordTextView: TextView
    private lateinit var definitionTextView: TextView
    private lateinit var tvProgress: TextView
    private lateinit var cardContentLayout: LinearLayout // Dùng để làm animation
    private lateinit var knownButton: MaterialButton
    private lateinit var unknownButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        setControl()
        initData()
        setEvent()
    }

    private fun setControl() {
        wordTextView = findViewById(R.id.wordTextView)
        definitionTextView = findViewById(R.id.definitionTextView)
        tvProgress = findViewById(R.id.tvProgress)
        cardContentLayout = findViewById(R.id.cardContentLayout)
        knownButton = findViewById(R.id.knownButton)
        unknownButton = findViewById(R.id.unknownButton)
    }

    private fun setEvent() {
        knownButton.setOnClickListener {
            markCurrentWordAsKnown()
            animateAndShowNext()
        }

        unknownButton.setOnClickListener {
            animateAndShowNext()
        }
    }

    private fun initData() {
        userId = UserSession.getUserId(this)
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        dictionaryWordDAO = DictionaryWordDAO(this)
        userWordStatusDAO = UserWordStatusDAO(this)
        studySessionDAO = StudySessionDAO(this)

        loadWordsToReview()
    }

    override fun onPause() {
        super.onPause()
        if (reviewedWordsCount > 0) {
            saveStudySession()
        }
    }

    private fun loadWordsToReview() {
        val allWords = dictionaryWordDAO.getAllWords()
        val knownWordIds = userWordStatusDAO.getKnownWordIds(userId).toSet()

        // Lọc từ chưa biết và xáo trộn
        wordsToReview = allWords.filter { it.id !in knownWordIds }.shuffled()
        currentWordIndex = 0
        reviewedWordsCount = 0

        if (wordsToReview.isNotEmpty()) {
            showWordAtIndex(currentWordIndex)
            enableButtons(true)
        } else {
            showEmptyState()
        }
    }

    private fun showWordAtIndex(index: Int) {
        if (index >= 0 && index < wordsToReview.size) {
            val word = wordsToReview[index]
            wordTextView.text = word.word
            definitionTextView.text = word.meaning

            // Cập nhật tiến độ: Ví dụ "Word 1 of 5"
            tvProgress.text = "Word ${index + 1} of ${wordsToReview.size}"
        }
    }

    // Hàm tạo hiệu ứng chuyển cảnh mượt mà
    private fun animateAndShowNext() {
        // Fade out (mờ đi)
        cardContentLayout.animate().alpha(0f).setDuration(150).withEndAction {
            showNextWordLogic()
            // Fade in (hiện lại)
            cardContentLayout.animate().alpha(1f).setDuration(150).start()
        }.start()
    }

    private fun showNextWordLogic() {
        reviewedWordsCount++
        currentWordIndex++
        if (currentWordIndex < wordsToReview.size) {
            showWordAtIndex(currentWordIndex)
        } else {
            Toast.makeText(this, "Chúc mừng! Bạn đã hoàn thành phiên ôn tập.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun markCurrentWordAsKnown() {
        if (currentWordIndex >= 0 && currentWordIndex < wordsToReview.size) {
            val word = wordsToReview[currentWordIndex]
            val status = UserWordStatus(
                userId = userId,
                wordId = word.id,
                status = "known",
                lastReviewed = Date(),
                reviewCount = 1
            )
            userWordStatusDAO.addOrUpdateStatus(status)
        }
    }

    private fun saveStudySession() {
        val session = StudySession(
            userId = userId,
            wordsCount = reviewedWordsCount,
            date = Date()
        )
        studySessionDAO.addSession(session)
    }



    private fun showEmptyState() {
        wordTextView.text = "Tuyệt vời!"
        definitionTextView.text = "Bạn đã học hết tất cả các từ trong từ điển."
        tvProgress.text = ""
        enableButtons(false)
    }

    private fun enableButtons(enable: Boolean) {
        knownButton.isEnabled = enable
        unknownButton.isEnabled = enable
        // Đổi màu nút khi disable để user dễ nhận biết (tuỳ chọn)
        knownButton.alpha = if (enable) 1.0f else 0.5f
        unknownButton.alpha = if (enable) 1.0f else 0.5f
    }
}