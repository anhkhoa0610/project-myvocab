package com.example.project.ui.vocabStatus

// Class DTO để hứng dữ liệu từ câu lệnh JOIN
data class Vocabulary(
    val id: Int,          // word_id
    val word: String,
    val meaning: String,
    val phonetic: String,
    var status: String    // status lấy từ bảng word_progress
)