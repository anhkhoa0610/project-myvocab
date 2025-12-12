package com.example.project.ui.add_edit_word

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity

class EditActivity : BaseActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etWord: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etPronunciation: EditText
    private lateinit var etPartOfSpeech: EditText

    private var currentId: Int = 0
    private var currentUserId: Int = 0 // Biến để lưu ID của từ đang sửa
    private lateinit var wordDAO: WordDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_layout)

        // Set title cho header
        setHeaderTitle("Edit Word")

        wordDAO = WordDAO(this)

        setControl()
        loadData()
        setEvent()
    }

    private fun setControl() {
        etWord = findViewById(R.id.etWord)
        etMeaning = findViewById(R.id.etMeaning)
        etPronunciation = findViewById(R.id.etPronunciation)
        etPartOfSpeech = findViewById(R.id.etPartOfSpeech)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun loadData() {
        // Lấy object Word được truyền sang từ MyVocabActivity
        val word = intent.getParcelableExtra<Word>("word")

        word?.let {
            currentId = it.id
            currentUserId = it.user_id
            etWord.setText(it.word)
            etMeaning.setText(it.meaning)
            etPronunciation.setText(it.pronunciation)
            etPartOfSpeech.setText(it.part_of_speech)
        }
    }

    private fun setEvent() {
        btnSave.setOnClickListener {
            val wordText = etWord.text.toString().trim()
            val meaning = etMeaning.text.toString().trim()
            val pronunciation = etPronunciation.text.toString().trim()
            val partOfSpeech = etPartOfSpeech.text.toString().trim()

            if (wordText.isEmpty() || meaning.isEmpty()) {
                Toast.makeText(this, "Thiếu dữ liệu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Tạo object với ID cũ để Database biết dòng nào mà sửa
            val updatedWord = Word(
                id = currentId,
                user_id = currentUserId,
                word = wordText,
                meaning = meaning,
                pronunciation = pronunciation,
                part_of_speech = partOfSpeech
            )

            // 2. Gọi DAO Update
            val result = wordDAO.updateWord(updatedWord)

            if (result > 0) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Báo Main reload lại
                finish()
            } else {
                Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}