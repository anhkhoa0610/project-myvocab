package com.example.project.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.model.User

class UserAdapter(
    private var list: ArrayList<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val cardIconBg: CardView = itemView.findViewById(R.id.cardIconBackground)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = list[position]

        // --- Avatar chữ cái đầu ---
        val firstChar = user.name.trim().firstOrNull()?.uppercaseChar() ?: '?'
        holder.tvAvatar.text = firstChar.toString()

        // --- Name & Email ---
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email

        // --- CreatedAt ---
        holder.tvCreatedAt.text = "Created: ${user.created_at.ifEmpty { "-" }}"

        // --- Role & màu sắc ---
        if (user.isAdmin()) {
            holder.tvRole.visibility = View.VISIBLE
            holder.tvRole.text = "ADMIN"
            holder.tvRole.setTextColor(Color.parseColor("#FF5722"))  // cam đậm
            holder.cardIconBg.setCardBackgroundColor(Color.parseColor("#FFCCBC"))  // cam nhạt
        } else {
            holder.tvRole.visibility = View.GONE
            // Avatar màu theo user ID để phân biệt
            val colors = listOf(
                "#F44336", "#E91E63", "#9C27B0",
                "#3F51B5", "#03A9F4", "#009688", "#FF9800"
            )
            val color = Color.parseColor(colors[user.id % colors.size])
            holder.cardIconBg.setCardBackgroundColor(color)
        }

        // --- Sự kiện click ---
        holder.btnEdit.setOnClickListener { onEditClick(user) }
        holder.btnDelete.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: ArrayList<User>) {
        list = newList
        notifyDataSetChanged()
    }
}
