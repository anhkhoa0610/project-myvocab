package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project.utils.WordStatus

class WordProgressDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Hàm 1: updateProgressOnView
     * Tự động gọi khi User lật thẻ xem từ vựng.
     * Logic:
     * - Tăng số lần xem (review_count).
     * - Nếu trạng thái đang là NEW -> Chuyển thành LEARNING.
     * - Nếu chưa có trong DB -> Tạo mới với trạng thái LEARNING.
     */
    fun updateProgressOnView(userId: Int, wordId: Int) {
        val db = dbHelper.writableDatabase

        try {
            // 1. Kiểm tra xem đã có record tiến độ của user cho từ này chưa
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM ${DatabaseHelper.TABLE_WORD_PROGRESS} WHERE ${DatabaseHelper.COLUMN_WP_USER_ID} = ? AND ${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
                arrayOf(userId.toString(), wordId.toString())
            )

            if (cursor.moveToFirst()) {
                // --- TRƯỜNG HỢP A: ĐÃ TỒN TẠI RECORD ---
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_ID))
                val currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_REVIEW_COUNT))
                val currentStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_STATUS))

                val values = ContentValues().apply {
                    // Tăng số lần xem lên 1
                    put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, currentCount + 1)

                    // Logic chuyển trạng thái:
                    // Chỉ khi đang là NEW thì mới tự động chuyển sang LEARNING.
                    // Nếu đang là MASTERED hoặc IGNORED thì giữ nguyên (không tự động reset).
                    if (currentStatus == WordStatus.NEW) {
                        put(DatabaseHelper.COLUMN_WP_STATUS, WordStatus.LEARNING)
                    }
                }

                db.update(
                    DatabaseHelper.TABLE_WORD_PROGRESS,
                    values,
                    "${DatabaseHelper.COLUMN_WP_ID} = ?",
                    arrayOf(id.toString())
                )

            } else {
                // --- TRƯỜNG HỢP B: CHƯA CÓ RECORD (Lần đầu tiên nhìn thấy) ---
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WP_USER_ID, userId)
                    put(DatabaseHelper.COLUMN_WP_WORD_ID, wordId)
                    put(DatabaseHelper.COLUMN_WP_STATUS, WordStatus.LEARNING) // Set ngay là LEARNING
                    put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 1) // Lần xem đầu tiên
                }
                db.insert(DatabaseHelper.TABLE_WORD_PROGRESS, null, values)
            }
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    /**
     * Hàm 2: markAsMastered
     * Gọi khi người dùng bấm nút "Đã thuộc" (Checkbox/Button).
     */
    fun markAsMastered(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.MASTERED)
    }

    /**
     * Hàm 3: markAsIgnored
     * Gọi khi người dùng bấm nút "Bỏ qua".
     */
    fun markAsIgnored(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.IGNORED)
    }

    /**
     * Hàm 4: resetToLearning
     * Gọi khi người dùng muốn học lại từ đã thuộc (Optional).
     */
    fun resetToLearning(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.LEARNING)
    }

    /**
     * Hàm nội bộ dùng chung để cập nhật Status
     */
    private fun updateStatus(userId: Int, wordId: Int, newStatus: String) {
        val db = dbHelper.writableDatabase
        try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_WP_STATUS, newStatus)
            }

            // Thử update record có sẵn
            val rows = db.update(
                DatabaseHelper.TABLE_WORD_PROGRESS,
                values,
                "${DatabaseHelper.COLUMN_WP_USER_ID} = ? AND ${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
                arrayOf(userId.toString(), wordId.toString())
            )

            // Nếu rows == 0 nghĩa là chưa có record nào -> Insert mới luôn
            if (rows == 0) {
                values.put(DatabaseHelper.COLUMN_WP_USER_ID, userId)
                values.put(DatabaseHelper.COLUMN_WP_WORD_ID, wordId)
                values.put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 0) // Mặc định 0 hoặc 1 tùy bạn
                db.insert(DatabaseHelper.TABLE_WORD_PROGRESS, null, values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    /**
     * Hàm lấy trạng thái hiện tại của 1 từ (để hiển thị lên UI, ví dụ checkbox đã tick chưa)
     */
    fun getWordStatus(userId: Int, wordId: Int): String {
        var status = WordStatus.NEW // Mặc định
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_WP_STATUS} FROM ${DatabaseHelper.TABLE_WORD_PROGRESS} WHERE ${DatabaseHelper.COLUMN_WP_USER_ID} = ? AND ${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
            arrayOf(userId.toString(), wordId.toString())
        )

        if (cursor.moveToFirst()) {
            status = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return status
    }
}