package com.example.project.utils

import android.content.Context
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.model.DictionaryWord
import com.example.project.data.model.QuizQuestion
import java.util.Random

class QuizGenerator(context: Context) {
    private val dictionaryDAO = DictionaryWordDAO(context)
    private val random = Random()

    fun generateQuizFromWords(words: List<DictionaryWord>, totalQuestions: Int): List<QuizQuestion> {
        val questions = ArrayList<QuizQuestion>()

        // Trộn danh sách từ và lấy số lượng cần thiết
        val targetWords = words.shuffled().take(totalQuestions)

        for ((index, word) in targetWords.withIndex()) {
            // Lấy 3 từ sai ngẫu nhiên từ database để làm đáp án nhiễu
            val wrongWords = dictionaryDAO.getRandomWords(3, word.id)
            if (wrongWords.size < 3) continue // Bỏ qua nếu không đủ dữ liệu

            // Random loại câu hỏi (0: Anh-Việt, 1: Việt-Anh, 2: Điền từ)
            val questionType = random.nextInt(3)

            val questionObj = when (questionType) {
                0 -> createWordToMeaningQuestion(index, word, wrongWords)
                1 -> createMeaningToWordQuestion(index, word, wrongWords)
                2 -> createFillBlankQuestion(index, word, wrongWords)
                else -> createWordToMeaningQuestion(index, word, wrongWords)
            }
            questions.add(questionObj)
        }
        return questions
    }

    // Dạng 1: Hỏi từ -> Chọn nghĩa
    private fun createWordToMeaningQuestion(id: Int, correct: DictionaryWord, wrong: List<DictionaryWord>): QuizQuestion {
        val options = wrong.map { it.meaning }.toMutableList()
        options.add(correct.meaning)
        options.shuffle()

        return QuizQuestion(
            id = id,
            quizId = 0, // Dynamic quiz, không cần ID cứng
            question = "Nghĩa của từ \"${correct.word}\" là gì?",
            answer = correct.meaning,
            options = options,
            difficulty = 1
        )
    }

    // Dạng 2: Hỏi nghĩa -> Chọn từ
    private fun createMeaningToWordQuestion(id: Int, correct: DictionaryWord, wrong: List<DictionaryWord>): QuizQuestion {
        val options = wrong.map { it.word }.toMutableList()
        options.add(correct.word)
        options.shuffle()

        return QuizQuestion(
            id = id,
            quizId = 0,
            question = "Từ nào có nghĩa là \"${correct.meaning}\"?",
            answer = correct.word,
            options = options,
            difficulty = 1
        )
    }

    // Dạng 3: Điền từ vào ví dụ (Nếu từ có ví dụ)
    private fun createFillBlankQuestion(id: Int, correct: DictionaryWord, wrong: List<DictionaryWord>): QuizQuestion {
        // Nếu không có ví dụ, quay về dạng 1
        if (correct.example_sentence.isBlank()) return createWordToMeaningQuestion(id, correct, wrong)

        // Thay thế từ chính trong câu ví dụ bằng "______"
        // (Lưu ý: Logic replace này đơn giản, thực tế cần xử lý viết hoa/thường tốt hơn)
        val blankedSentence = correct.example_sentence.replace(correct.word, "______", ignoreCase = true)

        val options = wrong.map { it.word }.toMutableList()
        options.add(correct.word)
        options.shuffle()

        return QuizQuestion(
            id = id,
            quizId = 0,
            question = "Điền từ còn thiếu: \n\"$blankedSentence\"",
            answer = correct.word,
            options = options,
            difficulty = 2
        )
    }
}
