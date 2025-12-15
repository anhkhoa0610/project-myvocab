package com.example.project.ui.games

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.project.R
import com.example.project.data.local.WordProgressDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession

class WritingGameActivity : BaseActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var edtAnswer: EditText
    private lateinit var btnCheck: Button
    private lateinit var btnHint: Button

    private var questionList = ArrayList<Word>()
    private var currentIndex = 0
    private var currentWord: Word? = null

    private var isCheckingState = true
    private var isRetryState = false
    private var isUsedHint = false

    private var userId: Int = -1

    private lateinit var wordProgressDAO: WordProgressDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_game)

        userId = UserSession.getUserId(this)
        if (userId <= 0) {
            Toast.makeText(this, "Invalid login session!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        wordProgressDAO = WordProgressDAO(this)

        setControl()

        questionList = intent.getParcelableArrayListExtra("list_word") ?: ArrayList()

        if (questionList.isEmpty()) {
            Toast.makeText(this, "No data available!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        questionList.shuffle()
        setupGame(0)
        setEvent()
    }


    private fun setControl() {
        tvQuestion = findViewById(R.id.tvQuestion)
        edtAnswer = findViewById(R.id.edtAnswer)
        btnCheck = findViewById(R.id.btnCheck)
        btnHint = findViewById(R.id.btnHint)
    }


    private fun setupGame(index: Int) {
        currentIndex = index
        currentWord = questionList[currentIndex]

        currentWord?.let {
            wordProgressDAO.updateProgressOnView(userId, it.id)
        }

        tvQuestion.text = currentWord?.meaning

        edtAnswer.setText("")
        edtAnswer.isEnabled = true
        edtAnswer.setTextColor(Color.BLACK)
        edtAnswer.hint = "Enter your answer..."

        isCheckingState = true
        isRetryState = false
        isUsedHint = false

        btnCheck.text = "Check"
        btnCheck.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#5C6BC0"))

        btnHint.isEnabled = true
        btnHint.alpha = 1f
    }


    private fun setEvent() {

        btnCheck.setOnClickListener {
            when {
                isRetryState -> resetForRetry()
                isCheckingState -> handleCheckAnswer()
                else -> handleNextQuestion()
            }
        }

        btnHint.setOnClickListener {
            val answer = currentWord?.word ?: return@setOnClickListener

            isUsedHint = true

            edtAnswer.setText(answer)
            edtAnswer.setSelection(answer.length)
            edtAnswer.setTextColor(Color.parseColor("#1976D2"))
            edtAnswer.isEnabled = false

            isRetryState = true
            isCheckingState = false

            btnCheck.text = "Try Again"
            btnCheck.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FF9800"))

            btnHint.isEnabled = false
            btnHint.alpha = 0.5f
        }
    }

    private fun handleCheckAnswer() {
        val userInput = normalizeInput(edtAnswer.text.toString())
        val correctAnswer = normalizeInput(currentWord?.word ?: "")

        if (userInput.isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show()
            return
        }

        if (userInput == correctAnswer) {
            onCorrectAnswer()
        } else {
            onWrongAnswer()
        }
    }

    private fun onCorrectAnswer() {
        edtAnswer.setTextColor(Color.parseColor("#4CAF50"))
        edtAnswer.isEnabled = false

        currentWord?.let {
            if (!isUsedHint) {
                wordProgressDAO.markAsMastered(userId, it.id)
            }
        }

        isCheckingState = false
        isRetryState = false

        btnCheck.text = "Continue"
        btnCheck.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#4CAF50"))

        btnHint.isEnabled = false
        btnHint.alpha = 0.5f

        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
    }

    private fun onWrongAnswer() {
        edtAnswer.setTextColor(Color.parseColor("#F44336"))

        Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            edtAnswer.setTextColor(Color.BLACK)
            edtAnswer.selectAll()
        }, 800)
    }

    private fun resetForRetry() {
        edtAnswer.setText("")
        edtAnswer.isEnabled = true
        edtAnswer.setTextColor(Color.BLACK)
        edtAnswer.requestFocus()

        isRetryState = false
        isCheckingState = true

        btnCheck.text = "Check"
        btnCheck.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#5C6BC0"))

        btnHint.isEnabled = true
        btnHint.alpha = 1f
    }

    private fun handleNextQuestion() {
        if (currentIndex < questionList.size - 1) {
            setupGame(currentIndex + 1)
        } else {
            showFinishDialog()
        }
    }

    private fun showFinishDialog() {
        AlertDialog.Builder(this)
            .setTitle("Completed!")
            .setMessage("You have finished the writing test.")
            .setPositiveButton("Finish") { _, _ -> finish() }
            .setNegativeButton("Play Again") { _, _ ->
                questionList.shuffle()
                setupGame(0)
            }
            .setCancelable(false)
            .show()
    }

    private fun normalizeInput(input: String): String {
        return input
            .trim()
            .replace("\\s+".toRegex(), " ")
            .lowercase()
    }
}
