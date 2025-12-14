package com.example.project.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.AddNewActivity
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession // Import UserSession

class MyVocabActivity : BaseActivity() {

    private var wordList = ArrayList<Word>()
    private var filteredWordList = ArrayList<Word>()
    private lateinit var adapter: WordAdapter
    private lateinit var listView: ListView
    private lateinit var wordDAO: WordDAO
    private lateinit var etSearch: EditText
    private lateinit var btnAdd: ImageView
    private val addWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadDataFromDB()
            Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show()
        }
    }

    val editWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadDataFromDB()
            Toast.makeText(this, "Đã cập nhật từ!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_vocab)

        setHeaderTitle("My Vocab")
        
        setControl()
        setEvent()
        loadDataFromDB()
    }

    private fun setControl() {
        listView = findViewById(R.id.lvWords)
        etSearch = findViewById(R.id.etSearch)
        wordDAO = WordDAO(this)
        
        // Setup Add Button
        btnAdd = ImageView(this)
        btnAdd.setImageResource(R.drawable.ic_add)
        btnAdd.setPadding(15, 15, 15, 15)
        
        val outValue = android.util.TypedValue()
        theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        btnAdd.setBackgroundResource(outValue.resourceId)
        frameRightAction.addView(btnAdd)
    }

    private fun setEvent() {
        setupSearchListener()
        
        // Add Button Click Event
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNewActivity::class.java)
            addWordLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDB()
    }

    private fun loadDataFromDB() {
        // Sử dụng UserSession đã import
        val userId = UserSession.getUserId(this)
        wordList = wordDAO.getWordsByUserId(userId)
        filteredWordList = ArrayList(wordList)

        adapter = WordAdapter(this, filteredWordList)
        listView.adapter = adapter
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterWords(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterWords(query: String) {
        filteredWordList.clear()

        if (query.isEmpty()) {
            // Nếu search rỗng, hiển thị tất cả
            filteredWordList.addAll(wordList)
        } else {
            // Lọc theo từ tiếng Anh hoặc nghĩa tiếng Việt
            val lowerCaseQuery = query.lowercase()
            for (word in wordList) {
                if (word.word.lowercase().contains(lowerCaseQuery) ||
                    word.meaning.lowercase().contains(lowerCaseQuery)) {
                    filteredWordList.add(word)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

}