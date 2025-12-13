package com.example.project.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.local.LevelDAO
import com.example.project.data.model.Category
import com.example.project.data.model.DictionaryWord
import com.example.project.data.model.Level
import com.example.project.ui.base.BaseActivity

class EditDictionaryWordActivity : BaseActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etWord: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etPronunciation: EditText
    private lateinit var etPartOfSpeech: EditText
    private lateinit var etExample: EditText
    private lateinit var spinnerLevel: Spinner
    private lateinit var spinnerCategory: Spinner

    private var currentId: Int = 0
    private lateinit var dictionaryWordDAO: DictionaryWordDAO
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var levelDAO: LevelDAO
    
    private var categoryList = ArrayList<Category>()
    private var levelList = ArrayList<Level>()
    private var selectedLevelId: Int = 1
    private var selectedCategoryId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dictionary_word)

        setHeaderTitle("Edit Dictionary Word")

        dictionaryWordDAO = DictionaryWordDAO(this)
        categoryDAO = CategoryDAO(this)
        levelDAO = LevelDAO(this)

        setControl()
        setupSpinners()
        loadData()
        setEvent()
    }

    private fun setControl() {
        etWord = findViewById(R.id.etWord)
        etMeaning = findViewById(R.id.etMeaning)
        etPronunciation = findViewById(R.id.etPronunciation)
        etPartOfSpeech = findViewById(R.id.etPartOfSpeech)
        etExample = findViewById(R.id.etExample)
        spinnerLevel = findViewById(R.id.spinnerLevel)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun setupSpinners() {
        // Setup Level Spinner
        levelList = levelDAO.getAllLevels()
        val levelAdapter = object : ArrayAdapter<Level>(
            this,
            android.R.layout.simple_spinner_item,
            levelList
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = levelList[position].name
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).text = levelList[position].name
                return view
            }
        }
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLevel.adapter = levelAdapter
        
        spinnerLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLevelId = levelList[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Setup Category Spinner
        categoryList = categoryDAO.getAllCategories()
        val categoryAdapter = object : ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            categoryList
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = categoryList[position].name
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).text = categoryList[position].name
                return view
            }
        }
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryId = categoryList[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadData() {
        val word = intent.getParcelableExtra<DictionaryWord>("word")

        word?.let {
            currentId = it.id
            etWord.setText(it.word)
            etMeaning.setText(it.meaning)
            etPronunciation.setText(it.pronunciation)
            etPartOfSpeech.setText(it.part_of_speech)
            etExample.setText(it.example_sentence)
            
            // Set spinner selections
            selectedLevelId = it.level_id
            selectedCategoryId = it.category_id
            
            // Find and set level spinner position
            val levelPosition = levelList.indexOfFirst { level -> level.id == it.level_id }
            if (levelPosition >= 0) {
                spinnerLevel.setSelection(levelPosition)
            }
            
            // Find and set category spinner position
            val categoryPosition = categoryList.indexOfFirst { cat -> cat.id == it.category_id }
            if (categoryPosition >= 0) {
                spinnerCategory.setSelection(categoryPosition)
            }
        }
    }

    private fun setEvent() {
        btnSave.setOnClickListener {
            val wordText = etWord.text.toString().trim()
            val meaning = etMeaning.text.toString().trim()
            val pronunciation = etPronunciation.text.toString().trim()
            val partOfSpeech = etPartOfSpeech.text.toString().trim()
            val example = etExample.text.toString().trim()

            if (wordText.isEmpty() || meaning.isEmpty()) {
                Toast.makeText(this, "Please enter word and meaning!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedWord = DictionaryWord(
                id = currentId,
                word = wordText,
                meaning = meaning,
                pronunciation = pronunciation,
                part_of_speech = partOfSpeech,
                level_id = selectedLevelId,
                category_id = selectedCategoryId,
                example_sentence = example,
                is_favorite = false
            )

            val result = dictionaryWordDAO.updateWord(updatedWord)

            if (result > 0) {
                Toast.makeText(this, "Dictionary word updated successfully!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error updating word!", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
