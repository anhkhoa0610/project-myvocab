package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project.data.model.Word

class WordDAO(context: Context) {

    // Gọi DatabaseHelper để chuẩn bị mở kết nối
    private val dbHelper = DatabaseHelper(context)

    /**
     * 1. THÊM TỪ MỚI (INSERT)
     * @param word: Đối tượng Word cần lưu
     * @return Long: ID của dòng vừa thêm (nếu -1 là lỗi)
     */
    fun addWord(word: Word): Long {
        // Mở DB ở chế độ Ghi (Write)
        val db = dbHelper.writableDatabase

        // ContentValues giống như một cái "hộp chứa" để đưa dữ liệu vào SQL
        val values = ContentValues().apply {
            // Cột ID tự tăng nên không cần put vào đây
            put(DatabaseHelper.COLUMN_WORD, word.word)
            put(DatabaseHelper.COLUMN_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_PART_OF_SPEECH, word.part_of_speech)
            // Lưu ý: Không lưu isSelected vì nó không có trong bảng
        }

        // Thực hiện lệnh Insert
        val result = db.insert(DatabaseHelper.TABLE_WORDS, null, values)

        // Đóng kết nối để tiết kiệm tài nguyên
        db.close()

        return result
    }

    /**
     * 2. LẤY TẤT CẢ TỪ (READ ALL)
     * @return ArrayList<Word>: Danh sách các từ có trong DB
     */
    fun getAllWords(): ArrayList<Word> {
        val listWords = ArrayList<Word>()

        // Mở DB ở chế độ Đọc (Read)
        val db = dbHelper.readableDatabase

        // Câu lệnh SQL: SELECT * FROM words
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_WORDS}"

        // Cursor là con trỏ, nó sẽ trỏ vào kết quả trả về
        val cursor: Cursor = db.rawQuery(query, null)

        // Di chuyển con trỏ đến dòng đầu tiên, nếu có dữ liệu thì bắt đầu vòng lặp
        if (cursor.moveToFirst()) {
            do {
                // --- BƯỚC MAPPING (QUAN TRỌNG) ---
                // Lấy dữ liệu thô từ các cột
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val wordText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD))
                val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEANING))
                val pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRONUNCIATION))
                val partOfSpeech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PART_OF_SPEECH))

                // Đổ vào đối tượng Word
                val wordObj = Word(
                    id = id, // Gán ID thật từ DB vào object
                    word = wordText,
                    meaning = meaning,
                    pronunciation = pronunciation,
                    part_of_speech = partOfSpeech,
                    isSelected = false // Mặc định khi mới load lên là chưa chọn
                )

                // Thêm vào danh sách
                listWords.add(wordObj)

            } while (cursor.moveToNext()) // Di chuyển đến dòng tiếp theo
        }

        // Đóng cursor và db
        cursor.close()
        db.close()

        return listWords
    }

    /**
     * 3. CẬP NHẬT TỪ (UPDATE)
     * @param word: Đối tượng Word đã chỉnh sửa (phải có ID tồn tại)
     * @return Int: Số dòng bị ảnh hưởng (thường là 1 nếu thành công)
     */
    fun updateWord(word: Word): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_WORD, word.word)
            put(DatabaseHelper.COLUMN_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_PART_OF_SPEECH, word.part_of_speech)
        }

        // Điều kiện Update: WHERE id = ?
        val result = db.update(
            DatabaseHelper.TABLE_WORDS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(word.id.toString()) // Truyền ID của word vào dấu ?
        )

        db.close()
        return result
    }

    /**
     * 4. XÓA TỪ (DELETE)
     * @param wordId: ID của từ muốn xóa
     * @return Int: Số dòng bị xóa
     */
    fun deleteWord(wordId: Int): Int {
        val db = dbHelper.writableDatabase

        // Điều kiện Delete: WHERE id = ?
        val result = db.delete(
            DatabaseHelper.TABLE_WORDS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(wordId.toString())
        )

        db.close()
        return result
    }
}