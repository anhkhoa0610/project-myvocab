package com.example.project.ui.flashcards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.project.R
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

        setHeaderTitle("Luyện tập")

        studyList = intent.getParcelableArrayListExtra("list_word") ?: ArrayList()

        setControl()

        if (studyList.isNotEmpty()) {
            loadCardData(0)
        } else {
            Toast.makeText(this, "Không có từ vựng nào!", Toast.LENGTH_SHORT).show()
        }

        setEvent()
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
        card.setOnClickListener {
            if (isFront) flip(front, back) else flip(back, front)
            isFront = !isFront
        }

        btnNext.setOnClickListener {
            if (currentIndex < studyList.size - 1) {
                currentIndex++
                loadCardData(currentIndex)
            } else {
                showFinishDialog()   // ⬅️ xử lý khi học hết
            }
        }

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadCardData(currentIndex)
            }
        }
    }

    private fun showFinishDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hoàn thành 🎉")
            .setMessage("Bạn đã học hết tất cả từ trong bài này!")
            .setPositiveButton("Về trang chủ") { _, _ ->
                val intent = Intent(this, MyVocabActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Học lại") { _, _ ->
                currentIndex = 0
                loadCardData(0)
            }
            .show()
    }

    private fun loadCardData(index: Int) {
        val word = studyList[index]

        tvEnWord.text = word.word
        tvPronun.text = word.pronunciation
        tvViMeaning.text = word.meaning

        tvCount.text = "${index + 1} / ${studyList.size}"

        // Reset card về mặt trước nếu đang ở mặt sau
        if (!isFront) {
            back.visibility = View.GONE
            front.visibility = View.VISIBLE
            front.rotationY = 0f
            isFront = true
        }

        btnPrev.isEnabled = index > 0
        btnNext.isEnabled = index < studyList.size - 1

        btnPrev.alpha = if (index > 0) 1.0f else 0.5f
        btnNext.alpha = if (index < studyList.size - 1) 1.0f else 0.5f
    }

    private fun flip(from: View, to: View) {
        val scale = applicationContext.resources.displayMetrics.density
        from.cameraDistance = 8000 * scale
        to.cameraDistance = 8000 * scale

        val flipOut = ObjectAnimator.ofFloat(from, "rotationY", 0f, 90f)
        flipOut.duration = 300

        val flipIn = ObjectAnimator.ofFloat(to, "rotationY", -90f, 0f)
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
}
