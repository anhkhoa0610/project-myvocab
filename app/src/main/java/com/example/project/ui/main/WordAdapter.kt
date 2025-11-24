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
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.EditActivity

class WordAdapter(
    private val activity: MainActivity,
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

        view.setOnClickListener {
            // 1. Tạo Intent trỏ đến màn hình bạn muốn (Ví dụ: FlashCardActivity)
            val intent = Intent(activity, FlashCardActivity::class.java)

            intent.putParcelableArrayListExtra("list_word", ArrayList(words))
            intent.putExtra("position", position) // Truyền thêm vị trí để biết đang chọn từ nào

            // 3. Khởi chạy màn hình mới
            activity.startActivity(intent)
        }

        // Hiển thị dữ liệu
        tvFirstLetter.text = word.word.firstOrNull()?.uppercase() ?: "?"
        tvWord.text = word.word
        tvMeaning.text = word.meaning
        tvPronun.text = word.pronunciation

        // Xử lý click nút Edit
        btnEdit.setOnClickListener {
            val intent = Intent(activity, EditActivity::class.java)
            intent.putExtra("word", word)
            intent.putExtra("position", position)
            activity.editWordLauncher.launch(intent)
        }

        // Xử lý click nút Delete
        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc muốn xóa từ \"${word.word}\"?")
                .setPositiveButton("Xóa") { _, _ ->
                    words.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(activity, "Đã xóa từ!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        return view
    }
}