package com.example.project.data.model

data class WordProgress(
    val id: Int,
    val userId: Int,
    val wordId: Int,
    val status: String, // NEW, LEARNING, MASTERED
    val reviewCount: Int
)