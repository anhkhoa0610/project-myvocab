package com.example.project.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.ui.main.WordAdapter
import com.example.project.data.model.Word

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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