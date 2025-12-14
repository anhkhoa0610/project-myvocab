package com.example.project.utils

object WordStatus {
    // 1. Các hằng số lưu trong Database
    const val NEW = "new"
    const val LEARNING = "learning"
    const val MASTERED = "mastered"
    const val IGNORED = "ignored"

    // 2. Danh sách hiển thị trên Spinner (UI)
    // Lưu ý: Thứ tự trong list này phải khớp với logic của 2 hàm bên dưới
    val displayList = listOf("New", "Learning", "Mastered")

    // 3. Hàm chuyển từ Code (DB) sang vị trí Spinner (0, 1, 2)
    fun getPositionFromStatus(status: String): Int {
        return when (status) {
            NEW -> 0
            LEARNING -> 1
            MASTERED -> 2
            else -> 0 // Mặc định là New nếu không tìm thấy hoặc là IGNORED
        }
    }

    // 4. Hàm chuyển từ vị trí Spinner (0, 1, 2) sang Code (DB)
    fun getStatusFromPosition(position: Int): String {
        return when (position) {
            0 -> NEW
            1 -> LEARNING
            2 -> MASTERED
            else -> NEW
        }
    }
}