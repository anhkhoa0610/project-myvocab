package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R

class WordAdapter(private val words: List<Word>) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val tvPronoun: TextView = itemView.findViewById(R.id.tvPronoun)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWord.text = word.word
        holder.tvMeaning.text = word.meaning
        holder.tvPronoun.text = word.pronoun
    }

    override fun getItemCount(): Int = words.size
}
