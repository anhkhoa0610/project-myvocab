package com.example.project.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
    private lateinit var adapter: WordAdapter
    private lateinit var listView: ListView
    private lateinit var wordDAO: WordDAO
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
        setupAddButton()

        listView = findViewById(R.id.lvWords)
        wordDAO = WordDAO(this)

        loadDataFromDB()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDB()
    }

    private fun loadDataFromDB() {
        // Sử dụng UserSession đã import
        val userId = UserSession.getUserId(this)
        wordList = wordDAO.getWordsByUserId(userId)

        adapter = WordAdapter(this, wordList)
        listView.adapter = adapter
    }

    private fun setupAddButton() {
        val btnAdd = ImageView(this)
        btnAdd.setImageResource(R.drawable.ic_add)
        btnAdd.setPadding(15, 15, 15, 15)

        val outValue = android.util.TypedValue()
        theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        btnAdd.setBackgroundResource(outValue.resourceId)

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNewActivity::class.java)
            addWordLauncher.launch(intent)
        }

        frameRightAction.addView(btnAdd)
    }
}