package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project.ui.vocabStatus.Vocabulary
import com.example.project.utils.WordStatus

class WordProgressDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun updateProgressOnView(userId: Int, wordId: Int) {
        val db = dbHelper.writableDatabase

        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM ${DatabaseHelper.TABLE_WORD_PROGRESS} WHERE ${DatabaseHelper.COLUMN_WP_USER_ID} = ? AND ${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
                arrayOf(userId.toString(), wordId.toString())
            )

            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_ID))
                val currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_REVIEW_COUNT))
                val currentStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WP_STATUS))

                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, currentCount + 1)

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
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WP_USER_ID, userId)
                    put(DatabaseHelper.COLUMN_WP_WORD_ID, wordId)
                    put(DatabaseHelper.COLUMN_WP_STATUS, WordStatus.LEARNING)
                    put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 1)
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

    fun markAsMastered(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.MASTERED)
    }

    fun markAsIgnored(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.IGNORED)
    }

    fun resetToLearning(userId: Int, wordId: Int) {
        updateStatus(userId, wordId, WordStatus.LEARNING)
    }

    fun getAllWordsWithStatus(userId: Int): List<Vocabulary> {
        val list = ArrayList<Vocabulary>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 
                w.${DatabaseHelper.COLUMN_WORD_ID}, 
                w.${DatabaseHelper.COLUMN_WORD_WORD}, 
                w.${DatabaseHelper.COLUMN_WORD_MEANING}, 
                w.${DatabaseHelper.COLUMN_WORD_PRONUNCIATION}, 
                IFNULL(wp.${DatabaseHelper.COLUMN_WP_STATUS}, '${WordStatus.NEW}') as calculated_status
            FROM ${DatabaseHelper.TABLE_WORDS} w
            LEFT JOIN ${DatabaseHelper.TABLE_WORD_PROGRESS} wp 
            ON w.${DatabaseHelper.COLUMN_WORD_ID} = wp.${DatabaseHelper.COLUMN_WP_WORD_ID} 
            AND wp.${DatabaseHelper.COLUMN_WP_USER_ID} = ?
            WHERE w.${DatabaseHelper.COLUMN_WORD_USER_ID} = ?
        """

        val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString(), userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
                val word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_PRONUNCIATION)
                val phonetic = if (pronunIndex != -1 && !cursor.isNull(pronunIndex)) cursor.getString(pronunIndex) else ""
                val status = cursor.getString(cursor.getColumnIndexOrThrow("calculated_status"))

                list.add(Vocabulary(id, word, meaning, phonetic, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getVocabularyByStatus(userId: Int, status: String): List<Vocabulary> {
        val list = ArrayList<Vocabulary>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT w.${DatabaseHelper.COLUMN_WORD_ID}, 
                   w.${DatabaseHelper.COLUMN_WORD_WORD}, 
                   w.${DatabaseHelper.COLUMN_WORD_MEANING}, 
                   w.${DatabaseHelper.COLUMN_WORD_PRONUNCIATION}, 
                   IFNULL(wp.${DatabaseHelper.COLUMN_WP_STATUS}, '${WordStatus.NEW}') as real_status
            FROM ${DatabaseHelper.TABLE_WORDS} w
            LEFT JOIN ${DatabaseHelper.TABLE_WORD_PROGRESS} wp 
            ON w.${DatabaseHelper.COLUMN_WORD_ID} = wp.${DatabaseHelper.COLUMN_WP_WORD_ID} 
            AND wp.${DatabaseHelper.COLUMN_WP_USER_ID} = ?
            WHERE real_status = ? AND w.${DatabaseHelper.COLUMN_WORD_USER_ID} = ?
        """

        val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString(), status, userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
                val word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION)
                val phonetic = if (cursor.isNull(pronunIndex)) "" else cursor.getString(pronunIndex)
                val currentStatus = cursor.getString(cursor.getColumnIndexOrThrow("real_status"))

                list.add(Vocabulary(id, word, meaning, phonetic, currentStatus))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateStatus(userId: Int, wordId: Int, newStatus: String) {
        val db = dbHelper.writableDatabase
        try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_WP_STATUS, newStatus)
            }

            val rows = db.update(
                DatabaseHelper.TABLE_WORD_PROGRESS,
                values,
                "${DatabaseHelper.COLUMN_WP_USER_ID} = ? AND ${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
                arrayOf(userId.toString(), wordId.toString())
            )

            if (rows == 0) {
                values.put(DatabaseHelper.COLUMN_WP_USER_ID, userId)
                values.put(DatabaseHelper.COLUMN_WP_WORD_ID, wordId)
                values.put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 0)
                db.insert(DatabaseHelper.TABLE_WORD_PROGRESS, null, values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun getWordStatus(userId: Int, wordId: Int): String {
        var status = WordStatus.NEW
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

    fun getWordCountByStatus(userId: Int): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT ${DatabaseHelper.COLUMN_WP_STATUS}, COUNT(*) as count
        FROM ${DatabaseHelper.TABLE_WORD_PROGRESS}
        WHERE ${DatabaseHelper.COLUMN_WP_USER_ID} = ?
        GROUP BY ${DatabaseHelper.COLUMN_WP_STATUS}
        """,
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            val status = cursor.getString(0)
            val count = cursor.getInt(1)
            result[status] = count
        }

        cursor.close()
        db.close()
        return result
    }
}