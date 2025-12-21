package com.example.project.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.LevelDAO
import com.example.project.data.model.Level
import com.example.project.ui.base.BaseActivity
import com.google.android.material.button.MaterialButton

class QuizSetupActivity : BaseActivity() {

    private lateinit var rgSource: RadioGroup
    private lateinit var spinnerLevels: Spinner
    private lateinit var layoutLevelSpinner: LinearLayout // Layout chứa spinner để chỉnh độ mờ
    private lateinit var btnStartQuiz: MaterialButton

    private lateinit var levelDAO: LevelDAO
    private var levels: List<Level> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_setup)
        setHeaderTitle("Level Selection \nTuan Kiet - Nhóm 2")

        // Ẩn Action Bar mặc định nếu bạn muốn dùng header trong layout
        supportActionBar?.hide()

        levelDAO = LevelDAO(this)

        initViews()
        loadLevels()
        setupEvents()
    }

    private fun initViews() {
        rgSource = findViewById(R.id.rgSource)
        spinnerLevels = findViewById(R.id.spinnerLevels)
        layoutLevelSpinner = findViewById(R.id.layoutLevelSpinner)
        btnStartQuiz = findViewById(R.id.btnStartQuiz)
    }

    private fun loadLevels() {
        levels = levelDAO.getAllLevels()
        if (levels.isEmpty()) {
            val defaultLevels = listOf("A1 - Beginner", "A2 - Elementary", "B1 - Intermediate", "B2 - Upper Inter")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, defaultLevels)
            spinnerLevels.adapter = adapter
        } else {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, levels.map { it.name })
            spinnerLevels.adapter = adapter
        }
    }

    private fun setupEvents() {
        // Mặc định disable spinner
        enableSpinner(false)

        rgSource.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbByLevel) {
                enableSpinner(true)
            } else {
                enableSpinner(false)
            }
        }

        btnStartQuiz.setOnClickListener {
            startQuiz()
        }
    }

    // Hàm phụ trợ để làm đẹp hiệu ứng Enable/Disable
    private fun enableSpinner(enable: Boolean) {
        spinnerLevels.isEnabled = enable
        layoutLevelSpinner.animate().alpha(if (enable) 1.0f else 0.4f).setDuration(200).start()
    }

    private fun startQuiz() {
        val intent = Intent(this, QuizActivity::class.java)
        var isValid = true

        when (rgSource.checkedRadioButtonId) {
            R.id.rbAllWords -> {
                intent.putExtra("MODE", "ALL")
            }
            R.id.rbFavorites -> {
                intent.putExtra("MODE", "FAVORITE")
            }
            R.id.rbByLevel -> {
                if (levels.isNotEmpty()) {
                    val selectedLevel = levels[spinnerLevels.selectedItemPosition]
                    intent.putExtra("MODE", "LEVEL")
                    intent.putExtra("LEVEL_ID", selectedLevel.id)
                } else {
                    intent.putExtra("MODE", "LEVEL")
                    intent.putExtra("LEVEL_ID", spinnerLevels.selectedItemPosition + 1)
                }
            }
            else -> {
                isValid = false
                Toast.makeText(this, "Vui lòng chọn nguồn từ vựng!", Toast.LENGTH_SHORT).show()
            }
        }

        if (isValid) {
            startActivity(intent)
            // Không finish() ở đây nếu bạn muốn user quay lại màn hình setup sau khi làm bài xong
            // finish()
        }
    }
}