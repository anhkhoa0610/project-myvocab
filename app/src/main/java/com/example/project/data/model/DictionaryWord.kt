package com.example.project.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DictionaryWord(
    val id: Int = 0,
    val word: String = "",
    val meaning: String = "",
    val pronunciation: String = "",
    val part_of_speech: String = "",
    val level_id: Int = 0,  // Foreign Key → levels(id)
    val category_id: Int = 0,  // Foreign Key → categories(id)
    val example_sentence: String = "",
    var is_favorite: Boolean = false
) : Parcelable {
    // Helper method để lấy level name
    fun getLevelName(): String {
        return when(level_id) {
            1 -> "A1"
            2 -> "A2"
            3 -> "B1"
            4 -> "B2"
            5 -> "C1"
            6 -> "C2"
            else -> ""
        }
    }
}
