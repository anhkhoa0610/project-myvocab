// File: com.example.project.ui.settings.SettingsActivity.kt

package com.example.project.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.project.R
import com.example.project.data.local.DatabaseHelper
import com.example.project.data.local.SettingsDAO
import com.example.project.ui.auth.ChangePasswordActivity
import com.example.project.ui.auth.LoginActivity // Giả sử LoginActivity là nơi quay về sau reset
import com.example.project.ui.base.BaseActivity

class SettingsActivity : BaseActivity() {

    private lateinit var settingsDAO: SettingsDAO

    // Khai báo Views
    private lateinit var rlChangePassword: RelativeLayout // 1. Thay đổi Mật khẩu
    private lateinit var rlTheme: RelativeLayout          // 2. Chỉnh Theme
    private lateinit var tvThemeSummary: TextView
    private lateinit var rlQuizCount: RelativeLayout      // 4. Chỉnh Số câu Quiz
    private lateinit var tvQuizCountSummary: TextView
    private lateinit var switchAutoFlip: SwitchCompat     // 5. Flashcard Auto-flip
    private lateinit var switchSoundEffects: SwitchCompat // 3. Sound Effects
    private lateinit var btnResetApp: Button              // 6. Reset App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setHeaderTitle("Setting")
        supportActionBar?.title = "Cài đặt Ứng dụng"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Bật nút Back

        settingsDAO = SettingsDAO(this)

        // 1. Ánh xạ Views
        bindViews()

        // 2. Khởi tạo trạng thái ban đầu của Views
        loadInitialSettings()

        // 3. Thiết lập Listeners
        setupListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Xử lý nút Back trên ActionBar
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun bindViews() {
        rlChangePassword = findViewById(R.id.setting_change_password)
        rlTheme = findViewById(R.id.setting_theme)
        tvThemeSummary = findViewById(R.id.tv_theme_summary)
        rlQuizCount = findViewById(R.id.setting_quiz_count)
        tvQuizCountSummary = findViewById(R.id.tv_quiz_count_summary)
        switchAutoFlip = findViewById(R.id.switch_flashcard_auto_flip)
        switchSoundEffects = findViewById(R.id.switch_sound_effects)
        btnResetApp = findViewById(R.id.btn_reset_app)
    }

    private fun loadInitialSettings() {
        // Load và cập nhật trạng thái các Views từ SettingsDAO
        updateThemeSummary(settingsDAO.getThemeMode())
        updateQuizCountSummary(settingsDAO.getQuizQuestionCount())
        switchAutoFlip.isChecked = settingsDAO.isFlashcardAutoFlipEnabled()
        switchSoundEffects.isChecked = settingsDAO.areSoundEffectsEnabled()
    }

    private fun setupListeners() {
        // 1. Thay đổi Mật khẩu Admin
        rlChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 2. Chỉnh Theme
        rlTheme.setOnClickListener { showThemeDialog() }

        // 3. Chỉnh Số câu Quiz
        rlQuizCount.setOnClickListener { showQuizCountDialog() }

        // 4. Bật/tắt Flashcard Auto-flip
        switchAutoFlip.setOnCheckedChangeListener { _, isChecked ->
            settingsDAO.saveFlashcardAutoFlip(isChecked)
            Toast.makeText(this, if (isChecked) "Tự động lật: Bật" else "Tự động lật: Tắt", Toast.LENGTH_SHORT).show()
        }

        // 5. Bật/tắt Sound Effect
        switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            settingsDAO.saveSoundEffectsEnabled(isChecked)
            Toast.makeText(this, if (isChecked) "Âm thanh: Bật" else "Âm thanh: Tắt", Toast.LENGTH_SHORT).show()
        }

        // 6. Reset toàn bộ App
        btnResetApp.setOnClickListener { showResetConfirmationDialog() }
    }

    // Hàm hiển thị Dialog chọn Theme
    private fun showThemeDialog() {
        val themes = arrayOf("Sáng", "Tối", "Theo Hệ thống")
        val themeValues = arrayOf("light", "dark", "system_default")
        val currentMode = settingsDAO.getThemeMode()
        val checkedItem = themeValues.indexOf(currentMode)

        AlertDialog.Builder(this)
            .setTitle("Chọn Giao Diện")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                val selectedMode = themeValues[which]
                settingsDAO.saveThemeMode(selectedMode) // Hàm này cũng gọi applyTheme
                updateThemeSummary(selectedMode)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Cập nhật text tóm tắt Theme
    private fun updateThemeSummary(mode: String) {
        val summaryText = when (mode) {
            "light" -> "Sáng"
            "dark" -> "Tối"
            else -> "Theo hệ thống"
        }
        tvThemeSummary.text = summaryText
    }

    // Hàm hiển thị Dialog chỉnh số câu Quiz
    private fun showQuizCountDialog() {
        val currentCount = settingsDAO.getQuizQuestionCount()
        val input = android.widget.EditText(this).apply {
            setText(currentCount.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(this)
            .setTitle("Số Câu Hỏi (5-50)")
            .setView(input)
            .setPositiveButton("Lưu") { dialog, _ ->
                val countText = input.text.toString()
                val newCount = countText.toIntOrNull()
                if (newCount != null && newCount in 5..50) {
                    settingsDAO.saveQuizQuestionCount(newCount)
                    updateQuizCountSummary(newCount)
                    Toast.makeText(this, "Đã lưu: $newCount câu.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Số câu không hợp lệ (5-50).", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Cập nhật text tóm tắt số câu Quiz
    private fun updateQuizCountSummary(count: Int) {
        tvQuizCountSummary.text = "$count câu"
    }

    // Hàm hiển thị Dialog xác nhận Reset
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đặt Lại Ứng Dụng")
            .setMessage("Bạn có chắc chắn muốn đặt lại tất cả cài đặt và dữ liệu học tập (Word, Category, DictionaryWord) về mặc định? Hành động này không thể hoàn tác.")
            .setPositiveButton("Đặt Lại") { _, _ ->
                performAppReset()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Hàm thực hiện Reset App
    private fun performAppReset() {
        // 1. Reset Settings về mặc định
        settingsDAO.resetToDefaults()

        // 2. Xóa Dữ liệu Ứng dụng (LƯU Ý: UserDAO cần được giữ lại)
        // Đây là chỗ bạn cần gọi các DAO khác để xóa dữ liệu (trừ UserDAO)
        val databaseHelper = DatabaseHelper(this) // Giả sử DatabaseHelper cần Context

        // TODO: THỰC HIỆN XÓA TẤT CẢ DỮ LIỆU CẦN RESET (trừ tài khoản)
        // databaseHelper.clearAllWordData()
        // databaseHelper.clearAllCategoryData()

        // 3. Quay lại màn hình Login và xóa stack
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        Toast.makeText(this, "Ứng dụng đã được đặt lại về mặc định.", Toast.LENGTH_LONG).show()
    }
}