package com.example.project.data.model

data class QuizQuestion(
    val id: Int,
    val quizId: Int,
    val question: String,
    val answer: String,
    val options: List<String>
)
