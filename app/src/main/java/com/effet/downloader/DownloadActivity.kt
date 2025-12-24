package com.effet.downloader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class DownloadActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var downloader: Downloader
    private var url: String = ""
    private lateinit var formatSpinner: Spinner
    private lateinit var qualitySpinner: Spinner
    private lateinit var formats: List<String>
    private lateinit var qualities: List<String>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startDownload()
            } else {
                Toast.makeText(this, "Notification permission is required to show download progress.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

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

        val rootView = window.decorView.findViewById<android.widget.LinearLayout>(android.R.id.content).getChildAt(0) as android.widget.LinearLayout

        formatSpinner = Spinner(this)
        formats = listOf("MP4", "MKV", "WEBM", "MP3", "M4A", "OPUS")
        val formatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formats)
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        formatSpinner.adapter = formatAdapter
        rootView.addView(formatSpinner, 1)

        qualitySpinner = Spinner(this)
        qualities = listOf("144p", "240p", "360p", "480p", "720p", "1080p", "4K", "Best")
        val qualityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, qualities)
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qualitySpinner.adapter = qualityAdapter
        qualitySpinner.setSelection(qualities.size - 1)
        rootView.addView(qualitySpinner, 2)

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        startDownload()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                startDownload()
            }
        }

        rootView.addView(startBtn, 3)
    }

    private fun startDownload() {
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
}
