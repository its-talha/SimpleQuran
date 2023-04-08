package com.mindorks.example.SimpleQuran

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : AppCompatActivity(), OnPageChangeListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar
    private lateinit var languageBtn: ImageView
    private lateinit var darkModeBtn: ImageView
    var appMode = false
    var pdfName = ""
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        toolbar = findViewById(R.id.toolbar)
        languageBtn = findViewById(R.id.imageButton1)
        darkModeBtn = findViewById(R.id.imageButton2)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)

//        PRDownloader.initialize(applicationContext)
        checkPdfAction(intent)

        languageBtn.setOnClickListener { View ->
            val intent = Intent(this,LanguageActivity::class.java)
            intent.putExtra("changed", true)
            startActivity(intent)
            finish()
        }

        darkModeBtn.setOnClickListener { View ->
            appMode = !appMode
            pdfView.setNightMode(appMode)
        }
    }

    private fun checkPdfAction(intent: Intent) {
        pdfName = intent.getStringExtra("pdf_name")

        var pageNo = 0
        if (pdfName == "English.pdf") {
            pageNo = 7
        } else if (pdfName == "Hindi.pdf") {
            pageNo = 11
        } else if (pdfName == "Urdu.pdf") {
            pageNo = 10
        }

        val last_page = sharedPreferences.getInt(pdfName, pageNo)


        pdfView.fromAsset(pdfName)
            .defaultPage(last_page)
            .swipeHorizontal(true)
            .pageSnap(true)
            .pageFling(true)
            .onPageChange(this)
            .load()
//        when (intent.getStringExtra("pdf_name")) {
//            "assets" -> {
//                showPdfFromAssets(FileUtils.getPdfNameFromAssets())
//            }
//            "storage" -> {
//                selectPdfFromStorage()
//            }
//            "internet" -> {
//                progressBar.visibility = View.VISIBLE
//                val fileName = "myFile.pdf"
//                downloadPdfFromInternet(
//                    FileUtils.getPdfUrl(),
//                    FileUtils.getRootDirPath(this),
//                    fileName
//                )
//            }
//        }
    }

    private fun showPdfFromAssets(pdfName: String) {
        pdfView.fromAsset(pdfName)
            .password(null)
            .defaultPage(0)
            .onPageError { page, _ ->
                Toast.makeText(
                    this@PdfViewActivity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    private fun selectPdfFromStorage() {
        Toast.makeText(this, "selectPDF", Toast.LENGTH_LONG).show()
        val browseStorage = Intent(Intent.ACTION_GET_CONTENT)
        browseStorage.type = "application/pdf"
        browseStorage.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(browseStorage, "Select PDF"), PDF_SELECTION_CODE
        )
    }

    private fun showPdfFromUri(uri: Uri?) {
        pdfView.fromUri(uri)
            .defaultPage(0)
            .spacing(10)
            .load()
    }

    private fun showPdfFromFile(file: File) {
        pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    this@PdfViewActivity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(this@PdfViewActivity, "downloadComplete", Toast.LENGTH_LONG)
                        .show()
                    val downloadedFile = File(dirPath, fileName)
                    progressBar.visibility = View.GONE
                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: Error?) {
                    Toast.makeText(
                        this@PdfViewActivity,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        sharedPreferences.edit().putInt(pdfName, page).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfFromStorage = data.data
            showPdfFromUri(selectedPdfFromStorage)
        }
    }

}
