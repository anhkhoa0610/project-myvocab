package com.example.project.ui.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.project.R
import com.example.project.data.model.DictionaryWord

class DictionaryAdapter(
    private val context: Context,
    private var wordList: ArrayList<DictionaryWord>,
    private val onFavoriteClick: (DictionaryWord) -> Unit,
    private val onSpeakClick: (String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = wordList.size

    override fun getItem(position: Int): DictionaryWord = wordList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val word = getItem(position)
        holder.bind(word, onFavoriteClick, onSpeakClick)

        return view
    }

    fun updateList(newList: ArrayList<DictionaryWord>) {
        wordList = newList
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        private val tvWord: TextView = view.findViewById(R.id.tvWord)
        private val tvPronunciation: TextView = view.findViewById(R.id.tvPronunciation)
        private val tvMeaning: TextView = view.findViewById(R.id.tvMeaning)
        private val tvExample: TextView = view.findViewById(R.id.tvExample)
        private val ivSpeaker: ImageView = view.findViewById(R.id.ivSpeaker)
        private val ivFavorite: ImageView = view.findViewById(R.id.ivFavorite)

        fun bind(
            word: DictionaryWord,
            onFavoriteClick: (DictionaryWord) -> Unit,
            onSpeakClick: (String) -> Unit
        ) {
            tvWord.text = word.word
            
            // Pronunciation + Part of Speech + Level
            val details = buildString {
                if (word.pronunciation.isNotEmpty()) append(word.pronunciation)
                if (word.part_of_speech.isNotEmpty()) {
                    if (isNotEmpty()) append(" • ")
                    append(word.part_of_speech)
                }
                val levelName = word.getLevelName()
                if (levelName.isNotEmpty()) {
                    if (isNotEmpty()) append(" • ")
                    append(levelName)
                }
            }
            tvPronunciation.text = details
            tvPronunciation.visibility = if (details.isEmpty()) View.GONE else View.VISIBLE
            
            tvMeaning.text = word.meaning
            
            // Example
            if (word.example_sentence.isNotEmpty()) {
                tvExample.text = "Example: ${word.example_sentence}"
                tvExample.visibility = View.VISIBLE
            } else {
                tvExample.visibility = View.GONE
            }
            
            // Speaker icon
            ivSpeaker.setOnClickListener {
                onSpeakClick(word.word)
            }
            
            // Favorite icon
            ivFavorite.setImageResource(
                if (word.is_favorite) android.R.drawable.star_big_on
                else android.R.drawable.star_big_off
            )
            
            ivFavorite.setOnClickListener {
                onFavoriteClick(word)
            }
        }
    }
}
