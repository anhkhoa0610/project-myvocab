package com.example.project.ui.itemDetail

import android.os.Bundle
import android.view.View // Import View để setVisibility
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.base.BaseActivity

// import com.example.project.ui.main.MyVocabActivity // Có thể không cần nếu EXTRA_WORD_ID được định nghĩa ở đây

class ItemDetailActivity : BaseActivity() {

    // Định nghĩa hằng số EXTRA_WORD_ID ở đây để không cần import MyVocabActivity
    companion object {
        const val EXTRA_WORD_ID = "word_id"
    }

    private lateinit var tvWord: TextView
    private lateinit var tvPronunciation: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvExample: TextView // Khai báo TextView cho ví dụ
    private lateinit var wordDAO: WordDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Ánh xạ các view
        tvWord = findViewById(R.id.tvWord)
        tvPronunciation = findViewById(R.id.tvPronunciation)
        tvMeaning = findViewById(R.id.tvMeaning)
//        tvExample = findViewById(R.id.tvExample) // Ánh xạ TextView ví dụ

        wordDAO = WordDAO(this)

        // Lấy ID của từ từ Intent sử dụng hằng số định nghĩa trong companion object
        val wordId = intent.getIntExtra(EXTRA_WORD_ID, -1)

        if (wordId != -1) {
            // Lấy thông tin chi tiết của từ từ database
            val word = wordDAO.getWordById(wordId)
            word?.let {
                displayWordDetails(it)
            } ?: run {
                // Xử lý trường hợp không tìm thấy từ
                Toast.makeText(this, "Không tìm thấy chi tiết từ", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            // Xử lý trường hợp không nhận được ID
            Toast.makeText(this, "Lỗi: Không có ID từ được truyền", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Thêm nút quay lại (Back button) trên ActionBar/Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayWordDetails(word: Word) {
        tvWord.text = word.word // Hiển thị từ

        // Xử lý hiển thị Phát âm (Pronunciation)
        if (!word.pronunciation.isNullOrBlank()) {
            tvPronunciation.text = word.pronunciation
            tvPronunciation.visibility = View.VISIBLE // Hiển thị nếu có dữ liệu
        } else {
            tvPronunciation.visibility = View.GONE // Ẩn nếu không có dữ liệu
        }

        // Hiển thị Nghĩa (Meaning)
        tvMeaning.text = word.meaning

        // Xử lý hiển thị Ví dụ (Example) - GIẢ SỬ Word có thuộc tính 'example'
        // Nếu lớp Word của bạn KHÔNG có thuộc tính 'example', hãy xóa phần này
        // và đảm bảo TextView tvExample đã được ẩn hoặc xóa khỏi layout.
//        if (!word.example.isNullOrBlank()) {
//            tvExample.text = word.example
//            tvExample.visibility = View.VISIBLE // Hiển thị nếu có dữ liệu
//        } else {
//            tvExample.visibility = View.GONE // Ẩn nếu không có dữ liệu
//        }

        // Nếu bạn có thuộc tính part_of_speech và muốn hiển thị nó,
        // bạn sẽ cần thêm TextView tương ứng trong layout và code ở đây.
        // Ví dụ:
        // if (!word.part_of_speech.isNullOrBlank()) {
        //     tvPartOfSpeech.text = word.part_of_speech
        //     tvPartOfSpeech.visibility = View.VISIBLE
        // } else {
        //     tvPartOfSpeech.visibility = View.GONE
        // }
    }

    // Xử lý khi người dùng nhấn nút quay lại trên ActionBar/Toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Quay lại Activity trước đó
        return true // Trả về true để cho biết sự kiện đã được xử lý
    }
}
