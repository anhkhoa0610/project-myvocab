package com.example.project.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.project.R
import com.example.project.data.local.WordDAO // Import DAO để xóa DB
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.EditActivity
import com.example.project.ui.itemDetail.ItemDetailMyVocabActivity

// import com.example.project.ui.study.FlashCardActivity // Bỏ comment dòng này nếu bạn để Flashcard trong gói study

class WordAdapter(
    private val activity: MyVocabActivity, // Activity để gọi launcher
    private val words: MutableList<Word>
) : ArrayAdapter<Word>(activity, 0, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_word, parent, false)

        val word = words[position]

        // Ánh xạ các views
        val tvFirstLetter = view.findViewById<TextView>(R.id.tvFirstLetter)
        val tvWord = view.findViewById<TextView>(R.id.tvWord)
        val tvMeaning = view.findViewById<TextView>(R.id.tvMeaning)
        val tvPronun = view.findViewById<TextView>(R.id.tvPronun)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        // Hiển thị dữ liệu
        // Lấy chữ cái đầu, viết hoa. Nếu null thì hiện dấu ?
        tvFirstLetter.text = word.word.firstOrNull()?.uppercase() ?: "?"
        tvWord.text = word.word
        tvMeaning.text = word.meaning
        tvPronun.text = word.pronunciation

        // 1. Xử lý click vào item (Chuyển sang màn hình Detail)
        view.setOnClickListener {
            val intent = Intent(activity, ItemDetailMyVocabActivity::class.java)
            // Truyền ID của từ sang Activity chi tiết
            intent.putExtra(ItemDetailMyVocabActivity.EXTRA_WORD_ID, word.id)
            activity.startActivity(intent)
        }

        // 2. Xử lý click nút Edit (Sửa)
        btnEdit.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("word", word)
            intent.putExtra("position", position)

            // --- SỬA DÒNG NÀY ---
            // Kiểm tra và ép kiểu context về MyVocabActivity để gọi editWordLauncher
            if (activity is MyVocabActivity) {
                activity.editWordLauncher.launch(intent)
            }
        }

        // 3. Xử lý click nút Delete (Xóa) - QUAN TRỌNG: Cập nhật xóa DB
        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc muốn xóa từ \"${word.word}\"?")
                .setPositiveButton("Xóa") { _, _ ->

                    // --- BẮT ĐẦU CODE MỚI ---
                    val wordDAO = WordDAO(activity)
                    val result = wordDAO.deleteWord(word.id) // Xóa trong Database

                    if (result > 0) {
                        // Nếu xóa DB thành công thì mới xóa trên List hiển thị
                        words.removeAt(position)
                        notifyDataSetChanged() // Cập nhật lại giao diện
                        Toast.makeText(activity, "Đã xóa thành công!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Lỗi: Không thể xóa trong Database!", Toast.LENGTH_SHORT).show()
                    }
                    // --- KẾT THÚC CODE MỚI ---

                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        return view
    }
}