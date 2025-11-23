package com.example.project.ui.add_edit_word

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.model.Word

class EditActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etWord: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etPronunciation: EditText
    private lateinit var etPartOfSpeech: EditText

    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_layout)

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
        val word = intent.getParcelableExtra<Word>("word")
        position = intent.getIntExtra("position", -1)

        word?.let {
            etWord.setText(it.word)
            etMeaning.setText(it.meaning)
            etPronunciation.setText(it.pronunciation)
            etPartOfSpeech.setText(it.part_of_speech)
        }
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

            val updatedWord = Word(word, meaning, pronunciation, partOfSpeech)

            val resultIntent = Intent()
            resultIntent.putExtra("updated_word", updatedWord)
            resultIntent.putExtra("position", position)
            setResult(RESULT_OK, resultIntent)

            Toast.makeText(this, "Đã cập nhật từ!", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}