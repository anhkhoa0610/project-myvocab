package com.example.project.ui.add_edit_word

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity

class AddNewActivity : BaseActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etWord: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etPronunciation: EditText
    private lateinit var etPartOfSpeech: EditText

    // Khai báo DAO
    private lateinit var wordDAO: WordDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew_layout)

        // Set title cho header
        setHeaderTitle("Add New Word")

        // Khởi tạo DAO
        wordDAO = WordDAO(this)

        setControl()
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

    private fun setEvent() {
        btnSave.setOnClickListener {
            val wordText = etWord.text.toString().trim()
            val meaning = etMeaning.text.toString().trim()
            val pronunciation = etPronunciation.text.toString().trim()
            val partOfSpeech = etPartOfSpeech.text.toString().trim()

            if (wordText.isEmpty() || meaning.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ từ và nghĩa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Tạo đối tượng Word (ID mặc định là 0, SQLite tự sinh)
            val newWord = Word(
                id = 0,
                word = wordText,
                meaning = meaning,
                pronunciation = pronunciation,
                part_of_speech = partOfSpeech
            )

            // 2. Gọi DAO để lưu vào Database
            val result = wordDAO.addWord(newWord)

            if (result > -1) {
                Toast.makeText(this, "Đã thêm từ mới vào DB!", Toast.LENGTH_SHORT).show()
                // Báo cho MyVocabActivity biết là OK để nó load lại DB
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi thêm từ!", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}