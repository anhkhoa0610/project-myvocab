package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.UserStats

class UserStatsDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Tạo stats cho user mới (gọi khi register)
    fun createStats(userId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_US_USER_ID, userId)
            put(DatabaseHelper.COLUMN_US_TOTAL_WORDS, 0)
            put(DatabaseHelper.COLUMN_US_LEARNED_WORDS, 0)
        }
        val result = db.insert(DatabaseHelper.TABLE_USER_STATS, null, values)
        db.close()
        return result
    }

    // Lấy stats của user
    fun getStats(userId: Int): UserStats? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USER_STATS} WHERE ${DatabaseHelper.COLUMN_US_USER_ID} = ?",
            arrayOf(userId.toString())
        )

        var stats: UserStats? = null
        if (cursor.moveToFirst()) {
            stats = UserStats(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_US_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_US_USER_ID)),
                totalWords = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_US_TOTAL_WORDS)),
                learnedWords = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_US_LEARNED_WORDS))
            )
        }
        cursor.close()
        db.close()
        return stats
    }

    // Tăng total_words (khi add word)
    fun incrementTotalWords(userId: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_USER_STATS} SET ${DatabaseHelper.COLUMN_US_TOTAL_WORDS} = ${DatabaseHelper.COLUMN_US_TOTAL_WORDS} + 1 WHERE ${DatabaseHelper.COLUMN_US_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
    }

    // Giảm total_words (khi delete word)
    fun decrementTotalWords(userId: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_USER_STATS} SET ${DatabaseHelper.COLUMN_US_TOTAL_WORDS} = CASE WHEN ${DatabaseHelper.COLUMN_US_TOTAL_WORDS} > 0 THEN ${DatabaseHelper.COLUMN_US_TOTAL_WORDS} - 1 ELSE 0 END WHERE ${DatabaseHelper.COLUMN_US_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
    }

    // Tăng learned_words (khi master word)
    fun incrementLearnedWords(userId: Int, count: Int = 1) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_USER_STATS} SET ${DatabaseHelper.COLUMN_US_LEARNED_WORDS} = ${DatabaseHelper.COLUMN_US_LEARNED_WORDS} + ? WHERE ${DatabaseHelper.COLUMN_US_USER_ID} = ?",
            arrayOf(count.toString(), userId.toString())
        )
        db.close()
    }

    // Giảm learned_words (khi unmaster word)
    fun decrementLearnedWords(userId: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_USER_STATS} SET ${DatabaseHelper.COLUMN_US_LEARNED_WORDS} = CASE WHEN ${DatabaseHelper.COLUMN_US_LEARNED_WORDS} > 0 THEN ${DatabaseHelper.COLUMN_US_LEARNED_WORDS} - 1 ELSE 0 END WHERE ${DatabaseHelper.COLUMN_US_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
    }
}
