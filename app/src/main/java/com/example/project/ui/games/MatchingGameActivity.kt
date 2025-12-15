package com.example.project.ui.games

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.model.CardType
import com.example.project.data.model.MatchingCard
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.main.MyVocabActivity

class MatchingGameActivity : BaseActivity() {

    private lateinit var rvCards: RecyclerView
    private lateinit var adapter: MatchingAdapter
    private var gameCards = ArrayList<MatchingCard>()

    private var originalWordList = ArrayList<Word>()

    private var firstSelectedCard: MatchingCard? = null
    private var firstSelectedPos: Int = -1
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_game)
        setHeaderTitle("Matching Game")

        rvCards = findViewById(R.id.rvMatchingCards)

        val wordList = intent.getParcelableArrayListExtra<Word>("list_word") ?: ArrayList()

        if (wordList.isNotEmpty()) {
            originalWordList = wordList
            setupGame(wordList)
        } else {
            Toast.makeText(this, "No data available!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupGame(words: ArrayList<Word>) {
        gameCards.clear()

        val leftColumn = mutableListOf<MatchingCard>()
        val rightColumn = mutableListOf<MatchingCard>()

        words.forEach { word ->
            leftColumn.add(
                MatchingCard(
                    id = word.id,
                    content = word.word,
                    type = CardType.EN
                )
            )
            rightColumn.add(
                MatchingCard(
                    id = word.id,
                    content = word.meaning,
                    type = CardType.VI
                )
            )
        }

        leftColumn.shuffle()
        rightColumn.shuffle()

        for (i in leftColumn.indices) {
            gameCards.add(leftColumn[i])
            gameCards.add(rightColumn[i])
        }

        adapter = MatchingAdapter(gameCards) { card, position ->
            handleCardClick(card, position)
        }

        rvCards.layoutManager = GridLayoutManager(this, 2)
        rvCards.adapter = adapter
    }

    private fun handleCardClick(clickedCard: MatchingCard, position: Int) {
        if (isProcessing || !clickedCard.isVisible) return

        if (clickedCard == firstSelectedCard) {
            clickedCard.isSelected = false
            firstSelectedCard = null
            adapter.notifyItemChanged(position)
            return
        }

        clickedCard.isSelected = true
        adapter.notifyItemChanged(position)

        if (firstSelectedCard == null) {
            firstSelectedCard = clickedCard
            firstSelectedPos = position
        } else {
            isProcessing = true

            val card1 = firstSelectedCard!!
            val card2 = clickedCard

            if (card1.id == card2.id) {
                Handler(Looper.getMainLooper()).postDelayed({
                    card1.isVisible = false
                    card2.isVisible = false
                    card1.isSelected = false
                    card2.isSelected = false

                    adapter.notifyItemChanged(firstSelectedPos)
                    adapter.notifyItemChanged(position)

                    resetTurn()
                    checkWinCondition()
                }, 500)

            } else {
                Toast.makeText(this, "Wrong match!", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    card1.isSelected = false
                    card2.isSelected = false

                    adapter.notifyItemChanged(firstSelectedPos)
                    adapter.notifyItemChanged(position)

                    resetTurn()
                }, 1000)
            }
        }
    }

    private fun resetTurn() {
        firstSelectedCard = null
        firstSelectedPos = -1
        isProcessing = false
    }

    private fun checkWinCondition() {
        val allHidden = gameCards.all { !it.isVisible }

        if (allHidden) {
            showFinishDialog()
        }
    }

    private fun showFinishDialog() {
        AlertDialog.Builder(this@MatchingGameActivity)
            .setTitle("Excellent! ")
            .setMessage(
                "You’ve matched all the cards.\n" +
                        "Now let’s challenge yourself with the writing test!"
            )

            .setPositiveButton("Start Writing Test") { _, _ ->
                val intent = Intent(
                    this@MatchingGameActivity,
                    WritingGameActivity::class.java
                )
                intent.putParcelableArrayListExtra("list_word", originalWordList)
                startActivity(intent)

                overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )

                finish()
            }

            .setNegativeButton("Back to Home") { _, _ ->
                val intent = Intent(
                    this@MatchingGameActivity,
                    MyVocabActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            .setNeutralButton("Play Again") { _, _ ->
                setupGame(originalWordList)
            }
            .setCancelable(false)
            .show()
    }
}
