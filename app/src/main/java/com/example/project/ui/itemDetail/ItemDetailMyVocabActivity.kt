package com.example.project.ui.itemDetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity

class ItemDetailMyVocabActivity : BaseActivity() {
    companion object {
        const val EXTRA_WORD_ID = "word_id"
    }

    private lateinit var tvWord: TextView
    private lateinit var tvPronunciation: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var wordDAO: WordDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail_myvocab)

        tvWord = findViewById(R.id.tvWord)
        tvPronunciation = findViewById(R.id.tvPronunciation)
        tvMeaning = findViewById(R.id.tvMeaning)

        wordDAO = WordDAO(this)

        val wordId = intent.getIntExtra(EXTRA_WORD_ID, -1)

        if (wordId != -1) {
            val word = wordDAO.getWordById(wordId)
            word?.let {
                displayWordDetails(it)
            } ?: run {
                Toast.makeText(this, "Không tìm thấy chi tiết từ", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có ID từ được truyền", Toast.LENGTH_SHORT).show()
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayWordDetails(word: Word) {
        tvWord.text = word.word

        if (!word.pronunciation.isNullOrBlank()) {
            tvPronunciation.text = word.pronunciation
            tvPronunciation.visibility = View.VISIBLE
        } else {
            tvPronunciation.visibility = View.GONE
        }

        tvMeaning.text = word.meaning
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
