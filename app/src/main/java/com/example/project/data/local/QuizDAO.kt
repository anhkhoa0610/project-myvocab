package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.project.data.model.Quiz
import com.example.project.data.model.QuizQuestion

class QuizDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

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

    // Trong hàm getQuestionsForQuiz
    fun getQuestionsForQuiz(quizId: Int): List<QuizQuestion> {
        val db = dbHelper.readableDatabase
        val questions = mutableListOf<QuizQuestion>()

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
            val difficulty = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QQ_DIFFICULTY))

            // Cập nhật lời gọi hàm tại đây
            val wrongAnswers = getWrongAnswers(db, answer, 3)
            val options = (wrongAnswers + answer).shuffled()

            questions.add(QuizQuestion(id, quizId, question, answer, options, difficulty))
        }
        cursor.close()
        db.close() // Bây giờ việc đóng db ở đây là an toàn và đúng đắn
        return questions
    }
    // Sửa hàm này
    private fun getWrongAnswers(db: SQLiteDatabase, correctAnswer: String, count: Int): List<String> {
        val wrongAnswers = mutableListOf<String>()
        val cursor = db.query(
            DatabaseHelper.TABLE_DICTIONARY,
            arrayOf(DatabaseHelper.COLUMN_DICT_MEANING),
            "${DatabaseHelper.COLUMN_DICT_MEANING} != ?",
            arrayOf(correctAnswer),
            null,
            null,
            "RANDOM()",
            count.toString()
        )

        while (cursor.moveToNext()) {
            wrongAnswers.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)))
        }
        cursor.close()
        // Không đóng db ở đây, vì nó được truyền từ bên ngoài
        return wrongAnswers
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
