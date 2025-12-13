package com.example.project.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.project.data.model.Quiz
import com.example.project.data.model.QuizQuestion

class QuizDAO(private val dbHelper: DatabaseHelper) {

    fun getQuizzesByLevel(levelId: Int): List<Quiz> {
        val db = dbHelper.readableDatabase
        val quizzes = mutableListOf<Quiz>()
        val cursor = db.query(
            DatabaseHelper.TABLE_QUIZZES,
            null,
            "${DatabaseHelper.COLUMN_QUIZ_LEVEL_ID} = ?",
            arrayOf(levelId.toString()),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUIZ_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUIZ_TITLE))
            val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUIZ_CATEGORY_ID))
            quizzes.add(Quiz(id, title, levelId, categoryId))
        }
        cursor.close()
        db.close()
        return quizzes
    }

    fun getQuestionsForQuiz(quizId: Int): List<QuizQuestion> {
        val db = dbHelper.readableDatabase
        val questions = mutableListOf<QuizQuestion>()
        // This is a simplified example. You'll need to handle options differently.
        // A raw query with joins might be better to fetch options from the dictionary or words table.
        val cursor = db.query(
            DatabaseHelper.TABLE_QUIZ_QUESTIONS,
            null,
            "${DatabaseHelper.COLUMN_QQ_QUIZ_ID} = ?",
            arrayOf(quizId.toString()),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QQ_ID))
            val question = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QQ_QUESTION))
            val answer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QQ_ANSWER))
            // You need to implement logic to get 3 other options.
            val options = listOf(answer, "Option 2", "Option 3", "Option 4").shuffled()
            questions.add(QuizQuestion(id, quizId, question, answer, options))
        }
        cursor.close()
        db.close()
        return questions
    }

    fun saveQuizResult(quizId: Int, userId: Int, score: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_QR_QUIZ_ID, quizId)
            put(DatabaseHelper.COLUMN_QR_USER_ID, userId)
            put(DatabaseHelper.COLUMN_QR_SCORE, score)
        }
        val id = db.insert(DatabaseHelper.TABLE_QUIZ_RESULTS, null, values)
        db.close()
        return id
    }
}
