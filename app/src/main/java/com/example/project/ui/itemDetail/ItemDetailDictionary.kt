package com.example.project.ui.itemDetail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.local.WordDAO
import com.example.project.data.model.DictionaryWord
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession.getUserId

class ItemDetailDictionary : BaseActivity() {

    companion object {
        const val EXTRA_DICTIONARY_WORD = "extra_dictionary_word"
    }

    // Views
    private lateinit var tvWord: TextView
    private lateinit var tvPronunciation: TextView
    private lateinit var tvTypeAndLevel: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvExample: TextView
    private lateinit var btnFavorite: ImageView
    private lateinit var layoutExample: View
    private lateinit var btnAddVocab: Button

    // Data & DAO
    private lateinit var currentWord: DictionaryWord
    private lateinit var dao: DictionaryWordDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail_dictionary)

        //title
        setHeaderTitle("Vocab Details")

        initViews()
        initData()
    }

    private fun initViews() {
        tvWord = findViewById(R.id.tvDetailWord)
        tvPronunciation = findViewById(R.id.tvDetailPronunciation)
        tvTypeAndLevel = findViewById(R.id.tvDetailTypeAndLevel)
        tvMeaning = findViewById(R.id.tvDetailMeaning)
        tvExample = findViewById(R.id.tvDetailExample)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnAddVocab = findViewById(R.id.btnAddVocab)
        layoutExample = tvExample
    }

    private fun initData() {
        dao = DictionaryWordDAO(this)
        val wordFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_DICTIONARY_WORD, DictionaryWord::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DICTIONARY_WORD)
        }
        if (wordFromIntent != null) {
            currentWord = wordFromIntent
            bindData(currentWord)
        } else {
            Toast.makeText(this, "Error: Could not load word details.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun bindData(word: DictionaryWord) {
        tvWord.text = word.word
        tvPronunciation.text = word.pronunciation
        val levelName = word.getLevelName()
        val partOfSpeech = word.part_of_speech

        val typeAndLevelBuilder = StringBuilder()
        if (partOfSpeech.isNotEmpty()) typeAndLevelBuilder.append(partOfSpeech)

        if (levelName.isNotEmpty()) {
            if (typeAndLevelBuilder.isNotEmpty()) typeAndLevelBuilder.append(" • ")
            typeAndLevelBuilder.append(levelName)
        }

        tvTypeAndLevel.text = typeAndLevelBuilder.toString()
        tvTypeAndLevel.visibility = if (typeAndLevelBuilder.isEmpty()) View.GONE else View.VISIBLE

        tvMeaning.text = word.meaning
        if (word.example_sentence.isNotEmpty()) {
            tvExample.text = word.example_sentence
            layoutExample.visibility = View.VISIBLE
        } else {
            layoutExample.visibility = View.GONE
        }

        updateFavoriteIcon()

        btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        btnAddVocab.setOnClickListener {
            addToMyVocab(currentWord)
        }
    }

    private fun updateFavoriteIcon() {
        if (currentWord.is_favorite) {
            btnFavorite.setImageResource(android.R.drawable.star_big_on)
        } else {
            btnFavorite.setImageResource(android.R.drawable.star_big_off)
        }
    }

    private fun toggleFavorite() {
        val success = dao.toggleFavorite(currentWord.id)

        if (success) {
            currentWord.is_favorite = !currentWord.is_favorite
            updateFavoriteIcon()

            val msg = if (currentWord.is_favorite) "Added to favorites" else "Removed from favorites"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update favorite", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToMyVocab(dictWord: DictionaryWord) {
        val wordDAO = WordDAO(this)

        // 1. Lấy ID người dùng hiện tại TRƯỚC (để dùng cho việc check)
        val currentUserId = com.example.project.utils.UserSession.getUserId(this)

        // 2. Lấy danh sách từ của RIÊNG User này thôi
        // (Thay vì getAllWords() lấy của cả thiên hạ)
        val myWords = wordDAO.getWordsByUserId(currentUserId)

        // 3. Kiểm tra xem trong danh sách CỦA MÌNH đã có từ này chưa
        val isExist = myWords.any { it.word.equals(dictWord.word, ignoreCase = true) }

        if (isExist) {
            Toast.makeText(this, "Từ '${dictWord.word}' đã có trong sổ tay của bạn rồi!", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Nếu chưa có thì tạo mới và thêm vào
        val newWord = Word(
            user_id = currentUserId, // Gán đúng chủ sở hữu
            word = dictWord.word,
            meaning = dictWord.meaning,
            pronunciation = dictWord.pronunciation,
            part_of_speech = dictWord.part_of_speech
        )

        val resultId = wordDAO.addWord(newWord)

        if (resultId > -1) {
            Toast.makeText(this, "Đã thêm '${dictWord.word}' vào sổ tay!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Lỗi: Không thể thêm từ này.", Toast.LENGTH_SHORT).show()
        }
    }
}