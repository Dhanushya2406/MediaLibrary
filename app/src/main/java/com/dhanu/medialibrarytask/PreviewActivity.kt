package com.dhanu.medialibrarytask

import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class PreviewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        webView = findViewById(R.id.webView)

        val fileUriString = intent.getStringExtra("file_uri")

        if (fileUriString != null) {
            displayFile(Uri.parse(fileUriString))
        } else {
            showError("Error: No file selected")
        }
    }

    private fun displayFile(fileUri: Uri) {
        try {
            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            } ?: showError("Error loading content file")
        } catch (e: Exception) {
            showError("Error loading file: ${e.message}")
        }
    }

    private fun showError(message: String) {
        webView.loadData("<h2 style='color:red;'>$message</h2>", "text/html", "UTF-8")
    }
}
