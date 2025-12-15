package com.example.project.ui.vocabStatus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.utils.WordStatus

class VocabularyStatusAdapter(
    private val context: Context,
    private var listVocab: List<Vocabulary>,
    private val onStatusChanged: (Int, String) -> Unit
) : RecyclerView.Adapter<VocabularyStatusAdapter.VocabularyViewHolder>() {

    inner class VocabularyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFirstLetter: TextView = itemView.findViewById(R.id.tvFirstLetter)
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val tvPronun: TextView = itemView.findViewById(R.id.tvPronun)
        val spinnerStatus: AppCompatSpinner = itemView.findViewById(R.id.spinnerStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocabulary_status, parent, false)
        return VocabularyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val item = listVocab[position]

        holder.tvWord.text = item.word
        holder.tvMeaning.text = item.meaning
        holder.tvPronun.text = item.phonetic
        if (item.word.isNotEmpty()) {
            holder.tvFirstLetter.text = item.word.first().toString().uppercase()
        }

        val adapter = StatusSpinnerAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            WordStatus.displayList
        )
        holder.spinnerStatus.adapter = adapter
        holder.spinnerStatus.onItemSelectedListener = null

        val currentPos = WordStatus.getPositionFromStatus(item.status)
        holder.spinnerStatus.setSelection(currentPos)

        holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newStatus = WordStatus.getStatusFromPosition(pos)
                if (newStatus != item.status) {
                    item.status = newStatus
                    onStatusChanged(item.id, newStatus)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = listVocab.size

    fun setData(newList: List<Vocabulary>) {
        listVocab = newList
        notifyDataSetChanged()
    }
}