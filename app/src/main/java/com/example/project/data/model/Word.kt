package com.example.project.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    val id: Int = 0,
    val word: String,
    val meaning: String,
    val pronunciation: String,
    val part_of_speech: String,
    var isSelected: Boolean = false,
): Parcelable