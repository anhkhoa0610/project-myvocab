package com.example.project.ui.admin

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
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.model.Category
import com.example.project.data.model.DictionaryWord

class DictionaryWordAdapter(
    private val activity: DictionaryManagementActivity,
    private val words: MutableList<DictionaryWord>,
    private val categories: ArrayList<Category>
) : ArrayAdapter<DictionaryWord>(activity, 0, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_admin_dictionary_word, parent, false)

        val word = words[position]

        // Map views
        val tvFirstLetter = view.findViewById<TextView>(R.id.tvFirstLetter)
        val tvWord = view.findViewById<TextView>(R.id.tvWord)
        val tvMeaning = view.findViewById<TextView>(R.id.tvMeaning)
        val tvPronun = view.findViewById<TextView>(R.id.tvPronun)
        val tvLevel = view.findViewById<TextView>(R.id.tvLevel)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvFirstLetter.text = word.word.firstOrNull()?.uppercase() ?: "?"
        tvWord.text = word.word
        tvMeaning.text = word.meaning
        tvPronun.text = word.pronunciation
        tvLevel.text = word.getLevelName()
        
        val category = categories.find { it.id == word.category_id }
        tvCategory.text = category?.name ?: "Unknown"
        
        category?.color?.let { colorString ->
            try {
                val color = android.graphics.Color.parseColor(colorString)
                tvCategory.setBackgroundColor(color)
            } catch (e: IllegalArgumentException) {
                tvCategory.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
            }
        } ?: run {
            tvCategory.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
        }

        btnEdit.setOnClickListener {
            val intent = Intent(context, EditDictionaryWordActivity::class.java)
            intent.putExtra("word", word)
            intent.putExtra("position", position)

            if (activity is DictionaryManagementActivity) {
                activity.editWordLauncher.launch(intent)
            }
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Delete Word")
                .setMessage("Are you sure you want to delete \"${word.word}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    val dictionaryWordDAO = DictionaryWordDAO(activity)
                    val result = dictionaryWordDAO.deleteWord(word.id)

                    if (result > 0) {
                        words.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(activity, "Word deleted successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Error: Cannot delete from database!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return view
    }
}
