package com.example.project.ui.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.local.WordProgressDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession
import com.example.project.utils.WordStatus

class StudySetupActivity : BaseActivity() {

    // UI
    private lateinit var lvSelection: ListView
    private lateinit var btnStart: Button

    // DAO
    private lateinit var wordDAO: WordDAO
    private lateinit var progressDAO: WordProgressDAO

    // Data
    private var allWords = ArrayList<Word>()
    private val wordStatusMap = mutableMapOf<Int, String>()

    // Adapter
    private lateinit var adapter: ArrayAdapter<Word>

    // User
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_setup)

        setHeaderTitle("Choose Lesson")

        // Get real userId from session
        userId = UserSession.getUserId(this)
        if (userId <= 0) {
            Toast.makeText(this, "Invalid login session!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initControls()

        wordDAO = WordDAO(this)
        progressDAO = WordProgressDAO(this)

        loadDataFromDB()
        setupEventStart()
    }

    private fun initControls() {
        lvSelection = findViewById(R.id.lvWordSelection)
        btnStart = findViewById(R.id.btnStartFlashcard)
    }

    private fun loadDataFromDB() {
        allWords = wordDAO.getWordsByUserId(userId)

        if (allWords.isEmpty()) {
            Toast.makeText(this, "No words available to study!", Toast.LENGTH_SHORT).show()
        }

        // Load status for each word
        allWords.forEach { word ->
            wordStatusMap[word.id] = progressDAO.getWordStatus(userId, word.id)
        }

        adapter = object : ArrayAdapter<Word>(this, R.layout.item_selection, allWords) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView
                    ?: layoutInflater.inflate(R.layout.item_selection, parent, false)

                val word = getItem(position)!!

                val tvWord = view.findViewById<TextView>(R.id.tvWordSel)
                val tvMeaning = view.findViewById<TextView>(R.id.tvMeaningSel)
                val checkBox = view.findViewById<CheckBox>(R.id.cbSelect)
                val btnReset = view.findViewById<ImageButton>(R.id.btnReset)

                tvWord.text = word.word
                tvMeaning.text = word.meaning

                val status = wordStatusMap[word.id] ?: WordStatus.NEW

                // Reset view (avoid recycle issues)
                checkBox.visibility = View.GONE
                btnReset.visibility = View.GONE
                view.setOnClickListener(null)

                if (status == WordStatus.MASTERED) {
                    // ===== MASTERED =====
                    btnReset.visibility = View.VISIBLE
                    btnReset.setOnClickListener {

                        progressDAO.resetToLearning(userId, word.id)
                        wordStatusMap[word.id] = WordStatus.LEARNING
                        word.isSelected = false

                        notifyDataSetChanged()

                        Toast.makeText(
                            context,
                            "Reset to learning: ${word.word}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    // ===== NOT MASTERED =====
                    checkBox.visibility = View.VISIBLE

                    checkBox.setOnCheckedChangeListener(null)
                    checkBox.isChecked = word.isSelected

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        word.isSelected = isChecked
                    }

                    view.setOnClickListener {
                        checkBox.isChecked = !checkBox.isChecked
                    }
                }

                return view
            }
        }

        lvSelection.adapter = adapter
    }

    private fun setupEventStart() {
        btnStart.setOnClickListener {

            val selectedList = allWords.filter { it.isSelected }

            when {
                selectedList.size < 2 -> {
                    Toast.makeText(
                        this,
                        "Please select at least 2 words",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                selectedList.size > 4 -> {
                    Toast.makeText(
                        this,
                        "You can select up to 4 words only",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            val intent = Intent(this, FlashCardActivity::class.java)
            intent.putParcelableArrayListExtra(
                "list_word",
                ArrayList(selectedList)
            )
            startActivity(intent)
        }
    }
}
