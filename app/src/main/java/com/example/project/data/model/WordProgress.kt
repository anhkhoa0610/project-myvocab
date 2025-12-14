package com.example.project.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordProgress(
    val id: Int = 0,             // Mặc định 0 cho record mới
    val userId: Int,             // Bắt buộc phải có
    val wordId: Int,             // Bắt buộc phải có
    var status: String = "new",  // Có thể thay đổi trạng thái
    var reviewCount: Int = 0     // Có thể thay đổi số lần xem
) : Parcelable