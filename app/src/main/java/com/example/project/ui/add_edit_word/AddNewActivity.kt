package com.example.project.ui.add_edit_word

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.model.Word

class AddNewActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etWord: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etPronunciation: EditText
    private lateinit var etPartOfSpeech: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew_layout)

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
            val word = etWord.text.toString().trim()
            val meaning = etMeaning.text.toString().trim()
            val pronunciation = etPronunciation.text.toString().trim()
            val partOfSpeech = etPartOfSpeech.text.toString().trim()

            if (word.isEmpty()) {
                etWord.setError("Thiếu dữ liệu từ!")
                return@setOnClickListener
            }

            if (meaning.isEmpty()) {
                etMeaning.setError("Thiếu dữ liệu nghĩa của từ!")
                return@setOnClickListener
            }

            if (pronunciation.isEmpty()) {
                etPronunciation.setError("Thiếu phát âm từ!")
                return@setOnClickListener
            }

            if (partOfSpeech.isEmpty()) {
                etPartOfSpeech.setError("Thiếu loại từ!")
                return@setOnClickListener
            }

            val newWord = Word(
                word,
                meaning,
                pronunciation,
                partOfSpeech
            )

            val resultIntent = Intent()
            resultIntent.putExtra("new_word", newWord)
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish() // Đóng Activity
        }
    }
}