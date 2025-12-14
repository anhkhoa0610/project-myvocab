package com.example.project.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.project.R
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.dictionary.DictionaryActivity
import com.example.project.ui.flashcards.StudySetupActivity
import com.example.project.ui.main.MyVocabActivity
import com.example.project.ui.quiz.LevelSelectionActivity
import com.example.project.ui.review.ReviewActivity // Import ReviewActivity
import com.example.project.ui.statistics.StatisticsActivity
import com.example.project.utils.UserSession

class DashboardActivity : BaseActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvRole: TextView
    private lateinit var cardMyVocab: CardView
    private lateinit var cardDictionary: CardView
    private lateinit var cardFlashcard: CardView
    private lateinit var cardStatistic: CardView
    private lateinit var cardStudyByLevel: CardView
    private lateinit var cardReviewWords: CardView // Thêm CardView cho ôn tập

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setHeaderTitle("Dashboard")
        
        initViews()
        setupWelcomeMessage()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Refresh welcome message when returning to dashboard
        setupWelcomeMessage()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvRole = findViewById(R.id.tvRole)
        cardMyVocab = findViewById(R.id.cardMyVocab)
        cardDictionary = findViewById(R.id.cardDictionary)
        cardFlashcard = findViewById(R.id.cardFlashcard)
        cardStatistic = findViewById(R.id.cardStatistic)
        cardStudyByLevel = findViewById(R.id.cardStudyByLevel)
        // Ánh xạ CardView mới. Giả sử bạn đã thêm một card với ID này vào layout
        cardReviewWords = findViewById(R.id.cardReviewWords) 
    }

    private fun setupWelcomeMessage() {
        val userName = UserSession.getUserName(this) ?: "User"
        val userRole = UserSession.getUserRole(this) ?: "user"
        
        tvWelcome.text = "Welcome, $userName!"
        tvRole.text = if (userRole == "admin") "👑 Admin" else "👤 User"
    }

    private fun setupListeners() {
        // My Vocabularies
        cardMyVocab.setOnClickListener {
            startActivity(Intent(this, MyVocabActivity::class.java))
        }

        // Dictionary
        cardDictionary.setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
        }

        // Flashcard
        cardFlashcard.setOnClickListener {
            startActivity(Intent(this, StudySetupActivity::class.java))
        }

        // Statistic
        cardStatistic.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        // Study by Level
        cardStudyByLevel.setOnClickListener {
            startActivity(Intent(this, LevelSelectionActivity::class.java))
        }

        // Review Words - Khởi chạy ReviewActivity
        cardReviewWords.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }
    }
}
