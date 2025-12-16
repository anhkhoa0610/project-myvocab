package com.example.project.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.LevelDAO
import com.example.project.data.local.QuizDAO
import com.example.project.data.model.Level
import com.example.project.ui.base.BaseActivity

class LevelSelectionActivity : BaseActivity() {

    private lateinit var levelsListView: ListView
    private lateinit var levelDAO: LevelDAO
    private lateinit var quizDAO: QuizDAO
    private var levels: List<Level> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_selection)

        initDAOs()
        setControl()
        loadLevels()
        setEvent()
    }

    private fun initDAOs() {
        levelDAO = LevelDAO(this)
        quizDAO = QuizDAO(this)
    }

    private fun setControl() {
        levelsListView = findViewById(R.id.levelsListView)
    }

    private fun loadLevels() {
        levels = levelDAO.getAllLevels()

        if (levels.isEmpty()) {
            Toast.makeText(this, "Không có cấp độ nào được tìm thấy!", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, levels.map { it.name })
        levelsListView.adapter = adapter
    }

    private fun setEvent() {
        levelsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedLevel = levels[position]
            val quizzes = quizDAO.getQuizzesByLevel(selectedLevel.id)

            if (quizzes.isNotEmpty()) {
                val intent = Intent(this, QuizActivity::class.java).apply {
                    putExtra("QUIZ_ID", quizzes[0].id)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không có bài kiểm tra nào cho cấp độ này!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
