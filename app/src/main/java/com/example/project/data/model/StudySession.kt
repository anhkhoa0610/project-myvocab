package com.example.project.data.model

import java.util.Date

data class StudySession(
    val id: Int = 0,
    val userId: Int,
    val wordsCount: Int,
    val date: Date
)
