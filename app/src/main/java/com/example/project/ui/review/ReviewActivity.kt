package com.example.project.ui.review

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.model.Word
import com.example.project.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var wordsToReview: MutableList<Word>
    private var currentWordIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWords()

        binding.knownButton.setOnClickListener {
            markCurrentWordAsKnown()
            showNextWord()
        }

        binding.unknownButton.setOnClickListener {
            showNextWord()
        }
    }

    private fun loadWords() {
        // TODO: Replace this with actual data from your database/repository
        wordsToReview = mutableListOf(
            Word(1, "Apple", "A fruit", "Easy", "Fruit", "Example sentence for apple.", "", 0, 0),
            Word(2, "Banana", "A yellow fruit", "Easy", "Fruit", "Example sentence for banana.", "", 0, 0),
            Word(3, "Cat", "A domestic animal", "Easy", "Animal", "Example sentence for cat.", "", 0, 0),
            Word(4, "Dog", "A loyal animal", "Easy", "Animal", "Example sentence for dog.", "", 0, 0)
        )

        if (wordsToReview.isNotEmpty()) {
            showWordAtIndex(currentWordIndex)
        } else {
            binding.wordTextView.text = "No words to review!"
            binding.definitionTextView.text = ""
            binding.knownButton.isEnabled = false
            binding.unknownButton.isEnabled = false
        }
    }

    private fun showWordAtIndex(index: Int) {
        if (index >= 0 && index < wordsToReview.size) {
            val word = wordsToReview[index]
            binding.wordTextView.text = word.word
            binding.definitionTextView.text = word.meaning
        }
    }

    private fun showNextWord() {
        currentWordIndex++
        if (currentWordIndex < wordsToReview.size) {
            showWordAtIndex(currentWordIndex)
        } else {
            // End of review session
            Toast.makeText(this, "Review session finished!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun markCurrentWordAsKnown() {
        if (currentWordIndex >= 0 && currentWordIndex < wordsToReview.size) {
            val word = wordsToReview[currentWordIndex]
            // TODO: Update the word's status in your database
            // For now, we'll just show a toast message
            Toast.makeText(this, "'${word.word}' marked as known!", Toast.LENGTH_SHORT).show()
        }
    }
}
