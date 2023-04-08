package com.mindorks.example.SimpleQuran

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class LanguageActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val initator = intent.getBooleanExtra("changed", false)
        if (sharedPreferences.contains("language") && !initator) {
            // If yes, launch the PdfActivity
            val lang = sharedPreferences.getString("language","")
            val intent = Intent(this, PdfViewActivity::class.java)
            when (lang) {
                "English.pdf" -> {
                    intent.putExtra("pdf_name", "English.pdf")
                }
                "Hindi.pdf" -> {
                    intent.putExtra("pdf_name", "Hindi.pdf")
                }
                "Urdu.pdf" -> {
                    intent.putExtra("pdf_name", "Urdu.pdf")
                }
            }
            startActivity(intent)
            finish()
        } else {
            val languages = arrayOf("English", "Hindi", "Urdu")
            val dialog = AlertDialog.Builder(this)
                .setTitle("Choose Language")
                .setItems(languages) { _, which ->
                    // redirect to screen that shows PDF based on language selection
                    val intent = Intent(this, PdfViewActivity::class.java)
                    when (which) {
                        0 -> {
                            intent.putExtra("pdf_name", "English.pdf")
                            sharedPreferences.edit().putString("language", "English.pdf").apply()
                        }
                        1 -> {
                            intent.putExtra("pdf_name", "Hindi.pdf")
                            sharedPreferences.edit().putString("language", "Hindi.pdf").apply()
                        }
                        2 -> {
                            intent.putExtra("pdf_name", "Urdu.pdf")
                            sharedPreferences.edit().putString("language", "Urdu.pdf").apply()
                        }
                    }
                    startActivity(intent)
                    finish()
                }
                .create()
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
        }
    }
}