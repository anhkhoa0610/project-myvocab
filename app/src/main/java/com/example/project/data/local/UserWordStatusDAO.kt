package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.UserWordStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserWordStatusDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun addOrUpdateStatus(status: UserWordStatus) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_WORD_USER_ID, status.userId)
            put(DatabaseHelper.COLUMN_USER_WORD_WORD_ID, status.wordId)
            put(DatabaseHelper.COLUMN_USER_WORD_STATUS, status.status)
            put(DatabaseHelper.COLUMN_USER_WORD_LAST_REVIEWED, status.lastReviewed?.let { dateFormat.format(it) })
            put(DatabaseHelper.COLUMN_USER_WORD_REVIEW_COUNT, status.reviewCount)
        }

        val existingId = getStatusId(status.userId, status.wordId)

        if (existingId != -1) {
            db.update(
                DatabaseHelper.TABLE_USER_WORD_STATUS,
                values,
                "${DatabaseHelper.COLUMN_USER_WORD_ID} = ?",
                arrayOf(existingId.toString())
            )
        } else {
            db.insert(DatabaseHelper.TABLE_USER_WORD_STATUS, null, values)
        }
        db.close()
    }

    fun getStatusId(userId: Int, wordId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER_WORD_STATUS,
            arrayOf(DatabaseHelper.COLUMN_USER_WORD_ID),
            "${DatabaseHelper.COLUMN_USER_WORD_USER_ID} = ? AND ${DatabaseHelper.COLUMN_USER_WORD_WORD_ID} = ?",
            arrayOf(userId.toString(), wordId.toString()),
            null, null, null
        )
        val id = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_WORD_ID))
        } else {
            -1
        }
        cursor.close()
        return id
    }

    fun getKnownWordIds(userId: Int): List<Int> {
        val wordIds = mutableListOf<Int>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER_WORD_STATUS,
            arrayOf(DatabaseHelper.COLUMN_USER_WORD_WORD_ID),
            "${DatabaseHelper.COLUMN_USER_WORD_USER_ID} = ? AND ${DatabaseHelper.COLUMN_USER_WORD_STATUS} = 'known'",
            arrayOf(userId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                wordIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_WORD_WORD_ID)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordIds
    }
}
