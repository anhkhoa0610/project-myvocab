package com.example.project.ui.vocabStatus

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StatusSpinnerAdapter(
    context: Context,
    resource: Int,
    objects: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    private val colorNew = Color.parseColor("#757575")
    private val colorLearning = Color.parseColor("#FF9800")
    private val colorMastered = Color.parseColor("#4CAF50")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        setColor(view, position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        setColor(view, position)
        return view
    }

    private fun setColor(view: View, position: Int) {
        val textView = view as TextView
        val itemText = getItem(position)

        when (itemText) {
            "New" -> {
                textView.setTextColor(colorNew)
            }
            "Learning" -> {
                textView.setTextColor(colorLearning)
            }
            "Mastered" -> {
                textView.setTextColor(colorMastered)
            }
            else -> textView.setTextColor(Color.BLACK)
        }
    }
}