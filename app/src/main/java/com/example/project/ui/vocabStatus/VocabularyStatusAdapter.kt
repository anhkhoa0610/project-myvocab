package com.example.project.ui.vocabStatus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.utils.WordStatus

// Model dữ liệu (Nên chuyển ra file riêng nếu có thể, tạm thời để đây)
data class Vocabulary(
    val id: Int,        // wordId
    val word: String,
    val meaning: String,
    val phonetic: String,
    var status: String  // Trạng thái: "new", "learning", "mastered"
)

class VocabularyStatusAdapter(
    private val context: Context,
    private var listVocab: List<Vocabulary>,
    // Callback: trả về wordId và status mới (String code)
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

        // 1. Bind dữ liệu text
        holder.tvWord.text = item.word
        holder.tvMeaning.text = item.meaning
        holder.tvPronun.text = item.phonetic
        if (item.word.isNotEmpty()) {
            holder.tvFirstLetter.text = item.word.first().toString().uppercase()
        }

        // 2. Setup Spinner
        // Sử dụng WordStatus.displayList thay vì StatusMapper
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            WordStatus.displayList // ["New", "Learning", "Mastered"]
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerStatus.adapter = adapter

        // 3. Set trạng thái hiện tại (Tắt listener trước khi setSelection để tránh loop)
        holder.spinnerStatus.onItemSelectedListener = null

        // Sử dụng WordStatus.getPositionFromStatus
        val currentPos = WordStatus.getPositionFromStatus(item.status)
        holder.spinnerStatus.setSelection(currentPos)

        // 4. Bắt sự kiện người dùng chọn mới
        holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                // Lấy status string từ vị trí mới
                val newStatus = WordStatus.getStatusFromPosition(pos)

                // Chỉ xử lý nếu trạng thái thực sự thay đổi
                if (newStatus != item.status) {
                    // Update model cục bộ để hiển thị đúng nếu scroll qua lại
                    item.status = newStatus

                    // Gọi callback báo cho Activity update Database
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