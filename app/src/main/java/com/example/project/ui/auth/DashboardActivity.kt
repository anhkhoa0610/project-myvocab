package com.example.project.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.project.R
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.dictionary.DictionaryActivity
import com.example.project.ui.flashcards.StudySetupActivity
import com.example.project.ui.main.MyVocabActivity
import com.example.project.ui.quiz.LevelSelectionActivity

class DashboardActivity : BaseActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvRole: TextView
    private lateinit var cardMyVocab: CardView
    private lateinit var cardDictionary: CardView
    private lateinit var cardFlashcard: CardView
    private lateinit var cardStatistic: CardView
    private lateinit var cardStudyByLevel: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setHeaderTitle("Dashboard")
        
        initViews()
        setupWelcomeMessage()
        setupListeners()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvRole = findViewById(R.id.tvRole)
        cardMyVocab = findViewById(R.id.cardMyVocab)
        cardDictionary = findViewById(R.id.cardDictionary)
        cardFlashcard = findViewById(R.id.cardFlashcard)
        cardStatistic = findViewById(R.id.cardStatistic)
        cardStudyByLevel = findViewById(R.id.cardStudyByLevel)
    }

    private fun setupWelcomeMessage() {
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        
        tvWelcome.text = "Welcome, $userName!"
        tvRole.text = if (userRole == "admin") "👑 Admin" else "👤 User"
    }

    private fun setupListeners() {
        // My Vocabularies
        cardMyVocab.setOnClickListener {
            val intent = Intent(this, MyVocabActivity::class.java)
            startActivity(intent)
        }

        // Dictionary
        cardDictionary.setOnClickListener {
            val intent = Intent(this, DictionaryActivity::class.java)
            startActivity(intent)
        }

        // Flashcard
        cardFlashcard.setOnClickListener {
            val intent = Intent(this, StudySetupActivity::class.java)
            startActivity(intent)
        }

        // Statistic (Coming soon)
        cardStatistic.setOnClickListener {
            Toast.makeText(this, "Statistic feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Study by Level
        cardStudyByLevel.setOnClickListener {
            val intent = Intent(this, LevelSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}
