package com.example.project.ui.flashcards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.project.R
import com.example.project.data.local.SettingsDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.games.MatchingGameActivity
import com.example.project.ui.main.MyVocabActivity

class FlashCardActivity : BaseActivity() {

    private lateinit var card: View
    private lateinit var front: View
    private lateinit var back: View

    private lateinit var tvEnWord: TextView
    private lateinit var tvPronun: TextView
    private lateinit var tvViMeaning: TextView
    private lateinit var tvCount: TextView

    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private var isFront = true
    private var studyList = ArrayList<Word>()
    private var currentIndex = 0

    private lateinit var settingsDAO: SettingsDAO
    private var isAutoFlipEnabled = false

    // ===== AUTO FLIP =====
    private val handler = Handler(Looper.getMainLooper())
    private val autoFlipRunnable = object : Runnable {
        override fun run() {
            if (isFront) {
                flip(front, back)
            } else {
                flip(back, front)
            }
            isFront = !isFront
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

        setHeaderTitle("Practice")

        settingsDAO = SettingsDAO(this)
        isAutoFlipEnabled = settingsDAO.isFlashcardAutoFlipEnabled()

        studyList =
            intent.getParcelableArrayListExtra("list_word") ?: ArrayList()

        setControl()
        setEvent()

        if (studyList.isNotEmpty()) {
            loadCardData(0)
            if (isAutoFlipEnabled) startAutoFlip()
        } else {
            Toast.makeText(this, "No vocabulary available!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setControl() {
        card = findViewById(R.id.cardContainer)
        front = findViewById(R.id.cardFront)
        back = findViewById(R.id.cardBack)

        tvEnWord = findViewById(R.id.tvEnWord)
        tvPronun = findViewById(R.id.tvPronun)
        tvViMeaning = findViewById(R.id.tvViMeaning)
        tvCount = findViewById(R.id.tvCount)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setEvent() {

        if (!isAutoFlipEnabled) {
            card.setOnClickListener {
                if (isFront) flip(front, back) else flip(back, front)
                isFront = !isFront
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < studyList.size - 1) {
                currentIndex++
                loadCardData(currentIndex)
            } else {
                showFinishDialog()
            }
        }

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadCardData(currentIndex)
            }
        }
    }


    private fun loadCardData(index: Int) {
        val word = studyList[index]

        tvEnWord.text = word.word
        tvPronun.text = word.pronunciation
        tvViMeaning.text = word.meaning
        tvCount.text = "${index + 1} / ${studyList.size}"

        // Reset card to front
        if (!isFront) {
            back.visibility = View.GONE
            front.visibility = View.VISIBLE
            front.rotationY = 0f
            isFront = true
        }

        // PREV
        btnPrev.isEnabled = index > 0
        btnPrev.alpha = if (index > 0) 1f else 0.5f

        // NEXT / FINISH
        btnNext.text =
            if (index == studyList.size - 1) "Finish" else "Next"
        btnNext.alpha = 1f

        // Auto flip restart
        if (isAutoFlipEnabled) {
            startAutoFlip()
        }
    }

    private fun showFinishDialog() {
        AlertDialog.Builder(this)
            .setTitle("Lesson Completed 🎉")
            .setMessage(
                "You have reviewed all the vocabulary!\n" +
                        "Try the matching game to test your memory."
            )
            .setPositiveButton("Play Matching Game") { _, _ ->
                val intent =
                    Intent(this, MatchingGameActivity::class.java)
                intent.putParcelableArrayListExtra(
                    "list_word",
                    studyList
                )
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Go to Home") { _, _ ->
                val intent =
                    Intent(this, MyVocabActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNeutralButton("Review Again") { _, _ ->
                currentIndex = 0
                loadCardData(0)
            }
            .setCancelable(false)
            .show()
    }

    private fun flip(from: View, to: View) {
        val scale = resources.displayMetrics.density
        from.cameraDistance = 8000 * scale
        to.cameraDistance = 8000 * scale

        val flipOut =
            ObjectAnimator.ofFloat(from, "rotationY", 0f, 90f)
        val flipIn =
            ObjectAnimator.ofFloat(to, "rotationY", -90f, 0f)

        flipOut.duration = 300
        flipIn.duration = 300

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                from.visibility = View.GONE
                to.visibility = View.VISIBLE
                flipIn.start()
            }
        })
        flipOut.start()
    }

    private fun startAutoFlip() {
        stopAutoFlip()
        handler.postDelayed(autoFlipRunnable, 3000)
    }

    private fun stopAutoFlip() {
        handler.removeCallbacks(autoFlipRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoFlip()
    }
}
