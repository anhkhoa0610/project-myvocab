// File: com.example.project.data.local.SettingsDAO.kt

package com.example.project.data.local

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.project.data.SettingsKey

class SettingsDAO(context: Context) {

    // Khởi tạo SharedPreferences với tên đã định nghĩa trong SettingsKey
    private val prefs = context.getSharedPreferences(
        SettingsKey.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // --- Lấy và Lưu Theme ---
    fun getThemeMode(): String {
        return prefs.getString(SettingsKey.KEY_THEME_MODE, SettingsKey.DEFAULT_THEME_MODE)
            ?: SettingsKey.DEFAULT_THEME_MODE
    }

    fun saveThemeMode(mode: String) {
        prefs.edit().putString(SettingsKey.KEY_THEME_MODE, mode).apply()
        applyTheme(mode)
    }

    // Áp dụng Theme ngay lập tức
    private fun applyTheme(mode: String) {
        when (mode) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    // --- Lấy và Lưu Số câu Quiz ---
    fun getQuizQuestionCount(): Int {
        return prefs.getInt(SettingsKey.KEY_QUIZ_QUESTION_COUNT, SettingsKey.DEFAULT_QUIZ_COUNT)
    }

    fun saveQuizQuestionCount(count: Int) {
        prefs.edit().putInt(SettingsKey.KEY_QUIZ_QUESTION_COUNT, count).apply()
    }

    // --- Lấy và Lưu Flashcard Auto-flip ---
    fun isFlashcardAutoFlipEnabled(): Boolean {
        return prefs.getBoolean(SettingsKey.KEY_FLASHCARD_AUTO_FLIP, SettingsKey.DEFAULT_AUTO_FLIP)
    }

    fun saveFlashcardAutoFlip(isEnabled: Boolean) {
        prefs.edit().putBoolean(SettingsKey.KEY_FLASHCARD_AUTO_FLIP, isEnabled).apply()
    }

    // --- Lấy và Lưu Sound Effects ---
    fun areSoundEffectsEnabled(): Boolean {
        return prefs.getBoolean(SettingsKey.KEY_SOUND_EFFECTS, SettingsKey.DEFAULT_SOUND_EFFECTS)
    }

    fun saveSoundEffectsEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(SettingsKey.KEY_SOUND_EFFECTS, isEnabled).apply()
    }

    // --- Reset về Mặc định ---
    fun resetToDefaults() {
        // Chỉ xóa các cài đặt đã được định nghĩa, giữ lại dữ liệu khác (nếu có)
        prefs.edit()
            .putString(SettingsKey.KEY_THEME_MODE, SettingsKey.DEFAULT_THEME_MODE)
            .putInt(SettingsKey.KEY_QUIZ_QUESTION_COUNT, SettingsKey.DEFAULT_QUIZ_COUNT)
            .putBoolean(SettingsKey.KEY_FLASHCARD_AUTO_FLIP, SettingsKey.DEFAULT_AUTO_FLIP)
            .putBoolean(SettingsKey.KEY_SOUND_EFFECTS, SettingsKey.DEFAULT_SOUND_EFFECTS)
            .apply()

        // Áp dụng lại theme mặc định
        applyTheme(SettingsKey.DEFAULT_THEME_MODE)
    }
}