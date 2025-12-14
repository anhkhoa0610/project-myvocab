package com.example.project.data.model

import java.util.Date

data class UserWordStatus(
    val id: Int = 0,
    val userId: Int,
    val wordId: Int,
    var status: String, // e.g., "known", "unknown"
    val lastReviewed: Date? = null,
    val reviewCount: Int = 0
)
