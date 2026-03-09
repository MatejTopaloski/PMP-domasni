package com.example.domasna1

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class MainActivity : AppCompatActivity() {

    private val dictionary = mutableMapOf<String, String>()     // mk -> en (нормализирано)
    private val reverseDictionary = mutableMapOf<String, String>() // en -> mk

    private lateinit var etMacedonian: TextInputEditText
    private lateinit var etEnglish: TextInputEditText
    private lateinit var etSearchQuery: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnSearch: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var container: LinearLayout
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Поврзување на views
        etMacedonian = findViewById(R.id.etMacedonian)
        etEnglish = findViewById(R.id.etEnglish)
        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSave = findViewById(R.id.btnSave)
        btnSearch = findViewById(R.id.btnSearch)
        btnClear = findViewById(R.id.btnClear)
        container = findViewById(R.id.tagsContainer)
        tvResult = findViewById(R.id.tvResult)

        loadDictionary()
        displayAllWords()

        btnSave.setOnClickListener {
            val mk = etMacedonian.text.toString().trim()
            val en = etEnglish.text.toString().trim()

            if (mk.isNotEmpty() && en.isNotEmpty()) {
                addNewWord(mk, en)
                etMacedonian.text?.clear()
                etEnglish.text?.clear()
            } else {
                Toast.makeText(this, "Внесете и македонски и англиски збор!", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener {
            performSearch()
        }

        btnClear.setOnClickListener {
            container.removeAllViews()
            Toast.makeText(this, "Листата е исчистена (речникот останува)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDictionaryFile(): File = File(filesDir, "dictionary.txt")

    private fun loadDictionary() {
        val file = getDictionaryFile()
        if (!file.exists()) {
            file.createNewFile()
            return
        }

        dictionary.clear()
        reverseDictionary.clear()

        file.forEachLine { line ->
            val parts = line.split("=")
            if (parts.size == 2) {
                val mk = parts[0].trim().lowercase()
                val en = parts[1].trim().lowercase()
                dictionary[mk] = en
                reverseDictionary[en] = mk
            }
        }
    }

    private fun addNewWord(mk: String, en: String) {
        val normMk = mk.lowercase()
        val normEn = en.lowercase()

        dictionary[normMk] = normEn
        reverseDictionary[normEn] = normMk

        getDictionaryFile().appendText("$normMk=$normEn\n")

        addNewTagRow(container, "$mk = $en")
        Toast.makeText(this, "Зборот е зачуван!", Toast.LENGTH_SHORT).show()
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim().lowercase()
        if (query.isEmpty()) {
            Toast.makeText(this, "Внесете збор за пребарување", Toast.LENGTH_SHORT).show()
            return
        }

        val result = dictionary[query] ?: reverseDictionary[query]

        if (result != null) {
            val text = if (dictionary.containsKey(query)) {
                "Англиски превод: $result"
            } else {
                "Македонски превод: $result"
            }
            tvResult.text = text
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        } else {
            tvResult.text = "Зборот не е пронајден"
            Toast.makeText(this, "Зборот не е пронајден во речникот", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayAllWords() {
        container.removeAllViews()
        dictionary.forEach { (mk, en) ->
            // Прикажуваме со оригинални големи букви (од клучот)
            addNewTagRow(container, "${mk.replaceFirstChar { it.uppercase() }} = ${en.replaceFirstChar { it.uppercase() }}")
        }
    }

    private fun addNewTagRow(container: LinearLayout, displayText: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 10) }
        }

        val tagBtn = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = displayText
            setBackgroundColor(resources.getColor(R.color.purple_pastel, null))
            setTextColor(resources.getColor(R.color.black, null))
            cornerRadius = 20
            isAllCaps = false
        }

        row.addView(tagBtn)
        container.addView(row)
    }
}