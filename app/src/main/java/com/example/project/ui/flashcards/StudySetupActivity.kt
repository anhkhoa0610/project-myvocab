package com.example.project.ui.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity

class StudySetupActivity : BaseActivity() {

    private lateinit var lvSelection: ListView
    private lateinit var btnStart: Button

    private lateinit var wordDAO: WordDAO
    private var allWords = ArrayList<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_setup)

        setHeaderTitle("Chọn bài học")

        initControls()

        wordDAO = WordDAO(this)
        loadDataFromDB()

        setupEventStart()
    }

    private fun initControls() {
        lvSelection = findViewById(R.id.lvWordSelection)
        btnStart = findViewById(R.id.btnStartFlashcard)
    }

    private fun loadDataFromDB() {
        val userId = com.example.project.utils.UserSession.getUserId(this)
        allWords = wordDAO.getWordsByUserId(userId)

        if (allWords.isEmpty()) {
            Toast.makeText(this, "Chưa có từ để học!", Toast.LENGTH_SHORT).show()
        }

        val adapter = object : ArrayAdapter<Word>(this, R.layout.item_selection, allWords) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_selection, parent, false)
                val word = getItem(position)!!

                val tvWord = view.findViewById<TextView>(R.id.tvWordSel)
                val tvMeaning = view.findViewById<TextView>(R.id.tvMeaningSel)
                val checkBox = view.findViewById<CheckBox>(R.id.cbSelect)

                tvWord.text = word.word
                tvMeaning.text = word.meaning

                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = word.isSelected

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    word.isSelected = isChecked
                }

                view.setOnClickListener {
                    checkBox.isChecked = !checkBox.isChecked
                }

                return view
            }
        }

        lvSelection.adapter = adapter
    }

    private fun setupEventStart() {
        btnStart.setOnClickListener {
            val selectedList = allWords.filter { it.isSelected } as ArrayList<Word>

            if (selectedList.isEmpty()) {
                Toast.makeText(this, "Bạn chưa chọn từ nào!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, FlashCardActivity::class.java)
            intent.putParcelableArrayListExtra("list_word", selectedList)
            startActivity(intent)
        }
    }
}
