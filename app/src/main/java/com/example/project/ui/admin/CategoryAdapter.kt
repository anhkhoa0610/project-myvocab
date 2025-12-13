package com.example.project.ui.admin

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.project.R
import com.example.project.data.model.Category

class CategoryAdapter(
    context: Context,
    private val categories: List<Category>,
    private val listener: OnCategoryActionListener
) : ArrayAdapter<Category>(context, 0, categories) {

    interface OnCategoryActionListener {
        fun onEditClick(category: Category)
        fun onDeleteClick(category: Category)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_category, parent, false)

        val category = getItem(position) ?: return view

        val cardIconBackground = view.findViewById<CardView>(R.id.cardIconBackground)
        val imgCategoryIcon = view.findViewById<ImageView>(R.id.imgCategoryIcon)
        val tvCategoryName = view.findViewById<TextView>(R.id.tvCategoryName)
        val tvCategoryDesc = view.findViewById<TextView>(R.id.tvCategoryDesc)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvCategoryName.text = category.name
        tvCategoryDesc.text = category.description

        try {
            val colorStr = if (category.color.isNotEmpty()) category.color else "#000000"
            val parsedColor = Color.parseColor(colorStr)

            imgCategoryIcon.setColorFilter(parsedColor)

            cardIconBackground.setCardBackgroundColor(Color.parseColor("#E0E0E0"))

        } catch (e: Exception) {
            imgCategoryIcon.setColorFilter(Color.BLACK)
            cardIconBackground.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
        }

        val iconName = if (category.icon.isNotEmpty()) category.icon else "ic_folder"
        val resourceId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        imgCategoryIcon.setImageResource(if (resourceId != 0) resourceId else R.drawable.ic_folder)

        btnEdit.setOnClickListener {
            listener.onEditClick(category)
        }

        btnDelete.setOnClickListener {
            listener.onDeleteClick(category)
        }

        return view
    }
}