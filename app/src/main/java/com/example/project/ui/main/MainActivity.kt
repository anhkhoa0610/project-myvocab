package com.example.project.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.AddNewActivity

class MainActivity : AppCompatActivity() {

    private val wordList = mutableListOf<Word>()
    private lateinit var btnAdd: ImageView
    private lateinit var adapter: WordAdapter
    private lateinit var listView: ListView

    private val addWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val newWord = result.data?.getParcelableExtra<Word>("new_word")
            newWord?.let {
                wordList.add(it)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val editWordLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedWord = result.data?.getParcelableExtra<Word>("updated_word")
            val position = result.data?.getIntExtra("position", -1) ?: -1

            if (updatedWord != null && position != -1) {
                wordList[position] = updatedWord
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã cập nhật từ!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setControl()
        setEvent()

        if (wordList.isEmpty()) {
            wordList.addAll(
                listOf(
                    Word("Cat", "Con mèo", "/kæt/", "Noun"),
                    Word("Dog", "Con chó", "/dɒɡ/", "Noun"),
                    Word("Run", "Chạy", "/rʌn/", "Verb")
                )
            )
        }

        listView = findViewById(R.id.lvWords)
        adapter = WordAdapter(this, wordList)
        listView.adapter = adapter
    }

    private fun setControl() {
        btnAdd = findViewById(R.id.btnAdd)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNewActivity::class.java)
            addWordLauncher.launch(intent)
        }
    }
}