package com.example.project.data

import com.example.project.data.model.WordProgress


object MockWordProgress {


    fun getMockData(): List<WordProgress> {
        return listOf(
            WordProgress(1, 1, 101, "NEW", 1),
            WordProgress(2, 1, 102, "NEW", 1),
            WordProgress(3, 1, 103, "LEARNING", 3),
            WordProgress(4, 1, 104, "LEARNING", 2),
            WordProgress(5, 1, 105, "MASTERED", 6),
            WordProgress(6, 1, 106, "MASTERED", 5),
            WordProgress(7, 1, 107, "MASTERED", 4)
        )
    }
}