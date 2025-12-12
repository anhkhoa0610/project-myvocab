package com.example.project.data.model

data class UserStats(
    val id: Int = 0,
    val userId: Int = 0,
    val totalWords: Int = 0,
    val learnedWords: Int = 0
) {
    // Helper để tính progress percentage
    fun getProgressPercentage(): Int {
        return if (totalWords > 0) {
            (learnedWords * 100) / totalWords
        } else {
            0
        }
    }
}
