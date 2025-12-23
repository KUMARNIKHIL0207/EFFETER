package com.effet.downloader

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("effet_prefs", Context.MODE_PRIVATE)

    fun setTermsAccepted(accepted: Boolean) {
        prefs.edit().putBoolean("terms_accepted", accepted).apply()
    }

    fun isTermsAccepted(): Boolean {
        return prefs.getBoolean("terms_accepted", false)
    }

    fun addDownloadItem(downloadId: String, fileName: String, format: String, quality: String) {
        val key = "download_$downloadId"
        prefs.edit().putString("${key}_name", fileName).apply()
        prefs.edit().putString("${key}_format", format).apply()
        prefs.edit().putString("${key}_quality", quality).apply()
        prefs.edit().putInt("${key}_progress", 0).apply()
        prefs.edit().putString("${key}_status", "Downloading").apply()
    }

    fun updateDownloadProgress(downloadId: String, progress: Int) {
        prefs.edit().putInt("download_${downloadId}_progress", progress).apply()
    }

    fun updateDownloadStatus(downloadId: String, status: String) {
        prefs.edit().putString("download_${downloadId}_status", status).apply()
    }

    fun getDownloads(): List<Map<String, String>> {
        val downloads = mutableListOf<Map<String, String>>()
        val allPrefs = prefs.all
        val keys = allPrefs.keys.filter { it.startsWith("download_") && it.endsWith("_name") }
        
        for (key in keys) {
            val id = key.removePrefix("download_").removeSuffix("_name")
            val map = mapOf(
                "id" to id,
                "name" to (allPrefs["download_${id}_name"] as? String ?: ""),
                "format" to (allPrefs["download_${id}_format"] as? String ?: ""),
                "quality" to (allPrefs["download_${id}_quality"] as? String ?: ""),
                "progress" to (allPrefs["download_${id}_progress"] as? Int ?: 0).toString(),
                "status" to (allPrefs["download_${id}_status"] as? String ?: "")
            )
            downloads.add(map)
        }
        return downloads
    }

    fun clearDownload(downloadId: String) {
        prefs.edit().apply {
            remove("download_${downloadId}_name")
            remove("download_${downloadId}_format")
            remove("download_${downloadId}_quality")
            remove("download_${downloadId}_progress")
            remove("download_${downloadId}_status")
            apply()
        }
    }
}
