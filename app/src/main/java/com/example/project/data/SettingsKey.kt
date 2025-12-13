// File: com.example.project.data.SettingsKey.kt

package com.example.project.data

/**
 * Định nghĩa các khóa hằng số cho SharedPreferences
 */
object SettingsKey {
    // Tên file SharedPreferences
    const val PREFS_NAME = "app_settings_prefs"

    // Các khóa lưu trữ
    const val KEY_THEME_MODE = "theme_mode"             // Giá trị: "light" / "dark" / "system_default"
    const val KEY_QUIZ_QUESTION_COUNT = "quiz_question_count" // Giá trị: Int
    const val KEY_FLASHCARD_AUTO_FLIP = "flashcard_auto_flip" // Giá trị: Boolean
    const val KEY_SOUND_EFFECTS = "sound_effects_enabled" // Giá trị: Boolean

    // Giá trị mặc định
    const val DEFAULT_THEME_MODE = "system_default"
    const val DEFAULT_QUIZ_COUNT = 10
    const val DEFAULT_AUTO_FLIP = false
    const val DEFAULT_SOUND_EFFECTS = true
}