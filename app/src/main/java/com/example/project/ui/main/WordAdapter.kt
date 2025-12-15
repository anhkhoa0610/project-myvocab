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
import com.example.project.data.local.WordDAO
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.EditActivity
import com.example.project.ui.itemDetail.ItemDetailMyVocabActivity

class WordAdapter(
    private val activity: MyVocabActivity,
    private val words: MutableList<Word>
) : ArrayAdapter<Word>(activity, 0, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_word, parent, false)

        val word = words[position]

        val tvFirstLetter = view.findViewById<TextView>(R.id.tvFirstLetter)
        val tvWord = view.findViewById<TextView>(R.id.tvWord)
        val tvMeaning = view.findViewById<TextView>(R.id.tvMeaning)
        val tvPronun = view.findViewById<TextView>(R.id.tvPronun)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvFirstLetter.text = word.word.firstOrNull()?.uppercase() ?: "?"
        tvWord.text = word.word
        tvMeaning.text = word.meaning
        tvPronun.text = word.pronunciation

        view.setOnClickListener {
            val intent = Intent(activity, ItemDetailMyVocabActivity::class.java)
            intent.putExtra(ItemDetailMyVocabActivity.EXTRA_WORD_ID, word.id)
            activity.startActivity(intent)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("word", word)
            intent.putExtra("position", position)

            if (activity is MyVocabActivity) {
                activity.editWordLauncher.launch(intent)
            }
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc muốn xóa từ \"${word.word}\"?")
                .setPositiveButton("Xóa") { _, _ ->
                    val wordDAO = WordDAO(activity)
                    val result = wordDAO.deleteWord(word.id)

                    if (result > 0) {
                        words.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(activity, "Đã xóa thành công!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Lỗi: Không thể xóa trong Database!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        return view
    }
}
