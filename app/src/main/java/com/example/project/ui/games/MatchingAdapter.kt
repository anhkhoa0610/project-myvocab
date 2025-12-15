package com.example.project.ui.games

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.model.MatchingCard

class MatchingAdapter(
    private var cardList: List<MatchingCard>,
    private val onCardClick: (MatchingCard, Int) -> Unit
) : RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: TextView = itemView.findViewById(R.id.tvCardContent)
        val cardView: CardView = itemView.findViewById(R.id.cardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matching_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cardList[position]

        holder.tvContent.text = card.content

        if (!card.isVisible) {
            holder.itemView.visibility = View.INVISIBLE
            holder.itemView.isClickable = false
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.isClickable = true
        }

        if (card.isSelected) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFD54F"))
            holder.tvContent.setTextColor(Color.BLACK)
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
            holder.tvContent.setTextColor(Color.parseColor("#333333"))
        }

        holder.itemView.setOnClickListener {
            onCardClick(card, position)
        }
    }

    override fun getItemCount(): Int = cardList.size
}