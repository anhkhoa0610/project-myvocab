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
    val level: String = "",  // A1, A2, B1, B2, C1, C2
    val category_id: Int = 0,  // Foreign Key → categories(id)
    val example_sentence: String = "",
    var is_favorite: Boolean = false
) : Parcelable
