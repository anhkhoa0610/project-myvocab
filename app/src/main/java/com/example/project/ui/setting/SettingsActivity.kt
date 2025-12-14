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
import com.example.project.ui.rename.RenameActivity

class SettingsActivity : BaseActivity() {

    private lateinit var settingsDAO: SettingsDAO

    // Khai báo Views
    private lateinit var rlChangePassword: RelativeLayout // 1. Thay đổi Mật khẩu
    private lateinit var rlTheme: RelativeLayout          // 2. Chỉnh Theme
    private lateinit var tvThemeSummary: TextView
    private lateinit var switchAutoFlip: SwitchCompat     // 5. Flashcard Auto-flip
    private lateinit var btnResetApp: Button              // 6. Reset App
    private lateinit var btnRename : RelativeLayout


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
        switchAutoFlip = findViewById(R.id.switch_flashcard_auto_flip)
        btnResetApp = findViewById(R.id.btn_reset_app)
        btnRename = findViewById(R.id.rename)

    }

    private fun loadInitialSettings() {
        // Load và cập nhật trạng thái các Views từ SettingsDAO
        updateThemeSummary(settingsDAO.getThemeMode())
        switchAutoFlip.isChecked = settingsDAO.isFlashcardAutoFlipEnabled()

    }

    private fun setupListeners() {
        // 1. Thay đổi Mật khẩu Admin
        rlChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 2. Chỉnh Theme
        rlTheme.setOnClickListener { showThemeDialog() }

        // 4. Bật/tắt Flashcard Auto-flip
        switchAutoFlip.setOnCheckedChangeListener { _, isChecked ->
            settingsDAO.saveFlashcardAutoFlip(isChecked)
            Toast.makeText(this, if (isChecked) "Tự động lật: Bật" else "Tự động lật: Tắt", Toast.LENGTH_SHORT).show()
        }

        // 6. Reset toàn bộ App
        btnResetApp.setOnClickListener { showResetConfirmationDialog() }

        btnRename.setOnClickListener {
            val intent = Intent(this, RenameActivity::class.java)
            startActivity(intent)
        }
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