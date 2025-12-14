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
}
