package com.example.project.ui.vocabStatus

data class Vocabulary(
    val id: Int,
    val word: String,
    val meaning: String,
    val phonetic: String,
    var status: String
)