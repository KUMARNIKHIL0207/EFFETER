package com.effet.downloader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var downloader: Downloader
    private var isHomeFragment = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = Prefs(this)
        downloader = Downloader(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val container = findViewById<View>(R.id.container)

        // Load home fragment by default
        loadHomeFragment()

        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (!isHomeFragment) {
                        loadHomeFragment()
                    }
                    true
                }
                R.id.nav_downloads -> {
                    if (isHomeFragment) {
                        loadDownloadsFragment()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadHomeFragment() {
        isHomeFragment = true
        val container = findViewById<android.widget.FrameLayout>(R.id.container)
        container.removeAllViews()
        val view = layoutInflater.inflate(R.layout.fragment_home, container, false)
        container.addView(view)

        val urlInput = view.findViewById<EditText>(R.id.urlInput)
        val downloadBtn = view.findViewById<Button>(R.id.downloadBtn)

        downloadBtn.setOnClickListener {
            val url = urlInput.text.toString()
            if (downloader.isValidUrl(url)) {
                startActivity(Intent(this, DownloadActivity::class.java).apply {
                    putExtra("url", url)
                })
            } else {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDownloadsFragment() {
        isHomeFragment = false
        val container = findViewById<android.widget.FrameLayout>(R.id.container)
        container.removeAllViews()
        val view = layoutInflater.inflate(R.layout.fragment_downloads, container, false)
        container.addView(view)

        val downloadsContainer = view.findViewById<LinearLayout>(R.id.downloadsContainer)
        val noDownloadsText = view.findViewById<android.widget.TextView>(R.id.noDownloadsText)

        val downloads = prefs.getDownloads()
        if (downloads.isEmpty()) {
            noDownloadsText.visibility = android.view.View.VISIBLE
        } else {
            downloadsContainer.removeAllViews()
            for (download in downloads) {
                val itemView = layoutInflater.inflate(R.layout.item_download, downloadsContainer, false)
                itemView.findViewById<android.widget.TextView>(R.id.fileName).text = download["name"]
                itemView.findViewById<android.widget.TextView>(R.id.format).text = download["format"]
                itemView.findViewById<android.widget.TextView>(R.id.quality).text = download["quality"]
                itemView.findViewById<android.widget.ProgressBar>(R.id.progressBar).progress = download["progress"]?.toIntOrNull() ?: 0
                itemView.findViewById<android.widget.TextView>(R.id.status).text = download["status"]
                itemView.findViewById<android.widget.TextView>(R.id.progress).text = "${download["progress"]}%"
                downloadsContainer.addView(itemView)
            }
        }
    }
}
