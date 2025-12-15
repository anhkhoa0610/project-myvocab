package com.example.project.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.example.project.R
import com.example.project.data.local.DatabaseHelper
import com.example.project.data.local.SettingsDAO
import com.example.project.ui.auth.ChangePasswordActivity
import com.example.project.ui.auth.LoginActivity
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.rename.RenameActivity

class SettingsActivity : BaseActivity() {

    // ================= DAO =================
    private lateinit var settingsDAO: SettingsDAO

    // ================= CONTROL =================
    private lateinit var rlChangePassword: RelativeLayout
    private lateinit var rlTheme: RelativeLayout
    private lateinit var tvThemeSummary: TextView
    private lateinit var switchAutoFlip: SwitchCompat
    private lateinit var btnResetApp: Button
    private lateinit var rlRename: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setHeaderTitle("Setting")
        supportActionBar?.title = "Cài đặt Ứng dụng"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setControl()
        setEvent()
    }

    // ================= ACTION BAR BACK =================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // ================= SET CONTROL =================
    private fun setControl() {
        settingsDAO = SettingsDAO(this)

        rlChangePassword = findViewById(R.id.setting_change_password)
        rlTheme = findViewById(R.id.setting_theme)
        tvThemeSummary = findViewById(R.id.tv_theme_summary)
        switchAutoFlip = findViewById(R.id.switch_flashcard_auto_flip)
        btnResetApp = findViewById(R.id.btn_reset_app)
        rlRename = findViewById(R.id.rename)

        loadInitialSettings()
    }

    // ================= SET EVENT =================
    private fun setEvent() {

        // 1. Đổi mật khẩu
        rlChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        // 2. Chọn Theme
        rlTheme.setOnClickListener {
            showThemeDialog()
        }

        // 3. Flashcard Auto-flip
        switchAutoFlip.setOnCheckedChangeListener { _, isChecked ->
            settingsDAO.saveFlashcardAutoFlip(isChecked)
            Toast.makeText(
                this,
                if (isChecked) "Tự động lật: Bật" else "Tự động lật: Tắt",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 4. Reset App
        btnResetApp.setOnClickListener {
            showResetConfirmationDialog()
        }

        // 5. Đổi tên
        rlRename.setOnClickListener {
            startActivity(Intent(this, RenameActivity::class.java))
        }
    }

    // ================= LOAD DATA =================
    private fun loadInitialSettings() {
        updateThemeSummary(settingsDAO.getThemeMode())
        switchAutoFlip.isChecked = settingsDAO.isFlashcardAutoFlipEnabled()
    }

    // ================= THEME =================
    private fun showThemeDialog() {
        val themes = arrayOf("Sáng", "Tối", "Theo hệ thống")
        val themeValues = arrayOf("light", "dark", "system_default")

        val currentMode = settingsDAO.getThemeMode()
        val checkedItem = themeValues.indexOf(currentMode)

        AlertDialog.Builder(this)
            .setTitle("Chọn Giao Diện")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                val selectedMode = themeValues[which]
                settingsDAO.saveThemeMode(selectedMode)
                updateThemeSummary(selectedMode)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun updateThemeSummary(mode: String) {
        tvThemeSummary.text = when (mode) {
            "light" -> "Sáng"
            "dark" -> "Tối"
            else -> "Theo hệ thống"
        }
    }

    // ================= RESET APP =================
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đặt Lại Ứng Dụng")
            .setMessage(
                "Bạn có chắc chắn muốn đặt lại tất cả cài đặt và dữ liệu học tập? " +
                        "Hành động này không thể hoàn tác."
            )
            .setPositiveButton("Đặt Lại") { _, _ ->
                performAppReset()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performAppReset() {
        // 1. Reset settings
        settingsDAO.resetToDefaults()

        // 2. Xóa dữ liệu học tập
        val databaseHelper = DatabaseHelper(this)
        // TODO: databaseHelper.clearAllWordData()
        // TODO: databaseHelper.clearAllCategoryData()

        // 3. Quay về Login
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        Toast.makeText(
            this,
            "Ứng dụng đã được đặt lại về mặc định.",
            Toast.LENGTH_LONG
        ).show()
    }
}
