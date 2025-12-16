package com.example.project.ui.dictionary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.model.DictionaryWord
import com.example.project.ui.base.BaseActivity
import com.example.project.ui.itemDetail.ItemDetailDictionary
import com.example.project.utils.TTSHelper

class DictionaryActivity : BaseActivity() {

    private lateinit var etSearch: EditText
    private lateinit var tvTabAll: TextView
    private lateinit var tvTabFavorites: TextView
    private lateinit var lvDictionaryWords: ListView
    private lateinit var tvEmptyState: TextView
    
    private lateinit var dictionaryDAO: DictionaryWordDAO
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var adapter: DictionaryAdapter
    private lateinit var ttsHelper: TTSHelper
    
    private var allWords = ArrayList<DictionaryWord>()
    private var currentTab = Tab.ALL

    private enum class Tab {
        ALL, FAVORITES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        setHeaderTitle("Dictionary \nAnh Khoa - Nhóm 2")
        
        setControl()
        setEvent()
        loadWords()
    }

    private fun setControl() {
        etSearch = findViewById(R.id.etSearch)
        tvTabAll = findViewById(R.id.tvTabAll)
        tvTabFavorites = findViewById(R.id.tvTabFavorites)
        lvDictionaryWords = findViewById(R.id.lvDictionaryWords)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        
        dictionaryDAO = DictionaryWordDAO(this)
        categoryDAO = CategoryDAO(this)
        ttsHelper = TTSHelper(this)
    }

    private fun setEvent() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    loadWords()
                } else {
                    searchWords(query)
                }
            }
        })

        tvTabAll.setOnClickListener {
            switchTab(Tab.ALL)
        }

        tvTabFavorites.setOnClickListener {
            switchTab(Tab.FAVORITES)
        }
    }

    private fun loadWords() {
        allWords = when (currentTab) {
            Tab.ALL -> dictionaryDAO.getAllWords()
            Tab.FAVORITES -> dictionaryDAO.getFavoriteWords()
        }
        
        updateUI()
    }

    private fun searchWords(query: String) {
        val searchResults = dictionaryDAO.searchWords(query)
        
        allWords = if (currentTab == Tab.FAVORITES) {
            ArrayList(searchResults.filter { it.is_favorite })
        } else {
            searchResults
        }
        
        updateUI()
    }

    private fun switchTab(tab: Tab) {
        currentTab = tab
        
        when (tab) {
            Tab.ALL -> {
                tvTabAll.setTextColor(resources.getColor(android.R.color.black, null))
                tvTabAll.textSize = 16f
                tvTabAll.setTypeface(null, android.graphics.Typeface.BOLD)
                
                tvTabFavorites.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                tvTabFavorites.textSize = 16f
                tvTabFavorites.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            Tab.FAVORITES -> {
                tvTabFavorites.setTextColor(resources.getColor(android.R.color.black, null))
                tvTabFavorites.textSize = 16f
                tvTabFavorites.setTypeface(null, android.graphics.Typeface.BOLD)
                
                tvTabAll.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                tvTabAll.textSize = 16f
                tvTabAll.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
        
        etSearch.text.clear()
        loadWords()
    }

    private fun updateUI() {
        if (allWords.isEmpty()) {
            lvDictionaryWords.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            
            tvEmptyState.text = when (currentTab) {
                Tab.ALL -> "No words found.\nTry searching for something!"
                Tab.FAVORITES -> "No favorite words yet.\nTap ⭐ to add favorites!"
            }
        } else {
            lvDictionaryWords.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
            
            adapter = DictionaryAdapter(
                this,
                allWords,
                onFavoriteClick = { word -> toggleFavorite(word) },
                onSpeakClick = { word -> ttsHelper.speak(word) }
            )
            lvDictionaryWords.adapter = adapter

            lvDictionaryWords.setOnItemClickListener { _, _, position, _ ->
                val selectedWord = allWords[position]
                val intent = Intent(this, ItemDetailDictionary::class.java)
                intent.putExtra(ItemDetailDictionary.EXTRA_DICTIONARY_WORD, selectedWord)
                startActivity(intent)
            }
        }
    }

    private fun toggleFavorite(word: DictionaryWord) {
        val success = dictionaryDAO.toggleFavorite(word.id)
        if (success) {
            word.is_favorite = !word.is_favorite
            adapter.notifyDataSetChanged()
            
            val message = if (word.is_favorite) {
                "Added to favorites!"
            } else {
                "Removed from favorites!"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            
            if (currentTab == Tab.FAVORITES) {
                loadWords()
            }
        }
    }
    
    override fun onDestroy() {
        ttsHelper.shutdown()
        super.onDestroy()
    }
}
