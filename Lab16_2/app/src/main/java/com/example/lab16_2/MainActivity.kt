package com.example.lab16_2

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val uri = Uri.parse("content://com.example.lab16")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
        setListeners()
    }

    private fun setListeners() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            val name = edBook.text.toString()
            val price = edPrice.text.toString()
            if (name.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                val values = ContentValues().apply {
                    put("book", name)
                    put("price", price)
                }
                val contentUri = contentResolver.insert(uri, values)
                if (contentUri != null) {
                    showToast("新增:$name,價格:$price")
                    clearInputs()
                } else {
                    showToast("新增失敗")
                }
            }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val name = edBook.text.toString()
            val price = edPrice.text.toString()
            if (name.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                val values = ContentValues().apply {
                    put("price", price)
                }
                val count = contentResolver.update(uri, values, name, null)
                if (count > 0) {
                    showToast("更新:$name,價格:$price")
                    clearInputs()
                } else {
                    showToast("更新失敗")
                }
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val name = edBook.text.toString()
            if (name.isEmpty()) {
                showToast("書名請勿留空")
            } else {
                val count = contentResolver.delete(uri, name, null)
                if (count > 0) {
                    showToast("刪除:$name")
                    clearInputs()
                } else {
                    showToast("刪除失敗")
                }
            }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            val name = edBook.text.toString()
            val selection = name.ifEmpty { null }
            val cursor = contentResolver.query(uri, null, selection, null, null)
            cursor ?: return@setOnClickListener
            cursor.moveToFirst()
            items.clear()
            showToast("共有${cursor.count}筆資料")
            repeat(cursor.count) {
                items.add("書名:${cursor.getString(0)}\t\t\t\t價格:${cursor.getInt(1)}")
                cursor.moveToNext()
            }
            adapter.notifyDataSetChanged()
            cursor.close()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clearInputs() {
        findViewById<EditText>(R.id.edBook).text.clear()
        findViewById<EditText>(R.id.edPrice).text.clear()
    }
}
