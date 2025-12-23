package com.effet.downloader

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DownloadActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var downloader: Downloader
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        prefs = Prefs(this)
        downloader = Downloader(this)

        url = intent.getStringExtra("url") ?: ""

        if (url.isEmpty()) {
            finish()
            return
        }

        // Create spinners and populate with formats/qualities
        val rootView = window.decorView.findViewById<android.widget.LinearLayout>(android.R.id.content).getChildAt(0) as android.widget.LinearLayout

        // Format spinner
        val formatSpinner = Spinner(this)
        val formats = listOf("MP4", "MKV", "WEBM", "MP3", "M4A", "OPUS")
        val formatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formats)
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        formatSpinner.adapter = formatAdapter
        rootView.addView(formatSpinner, 1)

        // Quality spinner
        val qualitySpinner = Spinner(this)
        val qualities = listOf("144p", "240p", "360p", "480p", "720p", "1080p", "4K", "Best")
        val qualityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, qualities)
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qualitySpinner.adapter = qualityAdapter
        qualitySpinner.setSelection(qualities.size - 1)
        rootView.addView(qualitySpinner, 2)

        // Start download button
        val startBtn = Button(this).apply {
            text = "Start Download"
            setTextColor(resources.getColor(R.color.background))
            setBackgroundColor(resources.getColor(R.color.primary_accent))
            setPadding(16, 16, 16, 16)
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16; marginStart = 16; marginEnd = 16 }
        }

        startBtn.setOnClickListener {
            val format = formats[formatSpinner.selectedItemPosition]
            val quality = qualities[qualitySpinner.selectedItemPosition]

            val downloadId = java.util.UUID.randomUUID().toString()
            prefs.addDownloadItem(downloadId, "media_${System.currentTimeMillis()}", format, quality)

            downloader.downloadMedia(
                url,
                format,
                quality,
                onProgress = { progress ->
                    prefs.updateDownloadProgress(downloadId, progress)
                },
                onComplete = { path ->
                    prefs.updateDownloadStatus(downloadId, "Completed")
                    runOnUiThread {
                        Toast.makeText(this, "Download completed: $path", Toast.LENGTH_LONG).show()
                    }
                },
                onError = { error ->
                    prefs.updateDownloadStatus(downloadId, "Failed: $error")
                    runOnUiThread {
                        Toast.makeText(this, "Download failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            )

            Toast.makeText(this, R.string.download_started, Toast.LENGTH_SHORT).show()
            startService(Intent(this, DownloadService::class.java).apply {
                putExtra("downloadId", downloadId)
            })
            finish()
        }

        rootView.addView(startBtn, 3)
    }
}
