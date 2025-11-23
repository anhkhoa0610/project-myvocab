package com.example.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R

class crud_layout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_layout)

        val recyclerView = findViewById<RecyclerView>(R.id.rvWords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val wordList = listOf(
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
            Word("Cat", "Con mèo", "/kat/"),
        )
        recyclerView.adapter = WordAdapter(wordList)
    }
}