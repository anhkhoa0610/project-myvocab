package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.StudySession
import java.text.SimpleDateFormat
import java.util.Locale

class StudySessionDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun addSession(session: StudySession): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SS_USER_ID, session.userId)
            put(DatabaseHelper.COLUMN_SS_WORDS_COUNT, session.wordsCount)
            put(DatabaseHelper.COLUMN_SS_DATE, dateFormat.format(session.date))
        }
        val result = db.insert(DatabaseHelper.TABLE_STUDY_SESSIONS, null, values)
        db.close()
        return result
    }
    fun getDailyStatsAsSessions(userId: Int): List<StudySession> {
        val list = mutableListOf<StudySession>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT
            COUNT(*) AS session_count,
            ${DatabaseHelper.COLUMN_SS_USER_ID},
            SUM(${DatabaseHelper.COLUMN_SS_WORDS_COUNT}) AS total_words,
            ${DatabaseHelper.COLUMN_SS_DATE}
        FROM ${DatabaseHelper.TABLE_STUDY_SESSIONS}
        WHERE ${DatabaseHelper.COLUMN_SS_USER_ID} = ?
        GROUP BY ${DatabaseHelper.COLUMN_SS_DATE}
        ORDER BY ${DatabaseHelper.COLUMN_SS_DATE} ASC
        """,
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            val sessionCount = cursor.getInt(0)
            val userIdDb = cursor.getInt(1)
            val totalWords = cursor.getInt(2)
            val date = dateFormat.parse(cursor.getString(3))!!

            list.add(
                StudySession(
                    id = sessionCount,          // số phiên học trong ngày
                    userId = userIdDb,
                    wordsCount = totalWords,    // tổng số từ trong ngày
                    date = date
                )
            )
        }

        cursor.close()
        db.close()
        return list
    }




}
