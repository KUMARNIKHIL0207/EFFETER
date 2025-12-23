package com.effet.downloader

import android.content.Context
import android.os.Environment
import java.io.File
import java.net.URL
import java.util.UUID
import kotlin.concurrent.thread

class Downloader(private val context: Context) {

    companion object {
        private const val DOWNLOAD_DIR = "Effet"
    }

    private val ytDlpBinary: File
    private val ffmpegBinary: File

    init {
        ytDlpBinary = File(context.filesDir, "yt-dlp")
        ffmpegBinary = File(context.filesDir, "ffmpeg")
        extractBinaries()
    }

    private fun extractBinaries() {
        if (!ytDlpBinary.exists()) {
            // In production, extract yt-dlp from assets
            // For now, mark as extracted placeholder
            ytDlpBinary.createNewFile()
        }
        if (!ffmpegBinary.exists()) {
            // In production, extract ffmpeg from assets
            // For now, mark as extracted placeholder
            ffmpegBinary.createNewFile()
        }
    }

    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            url.isNotEmpty() && (url.startsWith("http://") || url.startsWith("https://"))
        } catch (e: Exception) {
            false
        }
    }

    fun downloadMedia(
        url: String,
        format: String,
        quality: String,
        onProgress: (Int) -> Unit,
        onComplete: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        thread {
            try {
                val downloadDir = getDownloadDirectory()
                downloadDir.mkdirs()

                val fileName = sanitizeFileName("${UUID.randomUUID()}_media")
                val outputPath = File(downloadDir, fileName)

                // Build yt-dlp command
                val command = buildDownloadCommand(url, format, quality, outputPath.absolutePath)

                // Execute download
                executeCommand(command) { progress ->
                    onProgress(progress)
                }

                onComplete(outputPath.absolutePath)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private fun buildDownloadCommand(
        url: String,
        format: String,
        quality: String,
        outputPath: String
    ): List<String> {
        val qualityCode = when (quality) {
            "144p" -> "18"
            "240p" -> "17"
            "360p" -> "18"
            "480p" -> "59"
            "720p" -> "22"
            "1080p" -> "137"
            "4K" -> "313"
            else -> "best"
        }

        val formatArg = when (format.uppercase()) {
            "MP4" -> "mp4[ext=mp4]"
            "MKV" -> "mkv[ext=mkv]"
            "WEBM" -> "webm[ext=webm]"
            "MP3" -> "bestaudio[ext=mp3]"
            "M4A" -> "bestaudio[ext=m4a]"
            "OPUS" -> "bestaudio[ext=opus]"
            else -> "best"
        }

        return listOf(
            ytDlpBinary.absolutePath,
            "-f", formatArg,
            "-o", outputPath,
            url
        )
    }

    private fun executeCommand(
        command: List<String>,
        onProgress: (Int) -> Unit
    ) {
        try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val reader = process.inputStream.bufferedReader()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    // Parse progress from yt-dlp output
                    if (it.contains("%")) {
                        try {
                            val progress = it.substringAfter("[download]")
                                .substringBefore("%")
                                .trim()
                                .toFloatOrNull()?.toInt() ?: 0
                            onProgress(progress)
                        } catch (e: Exception) {
                            // Silently ignore parsing errors
                        }
                    }
                }
            }

            process.waitFor()
        } catch (e: Exception) {
            throw Exception("Download failed: ${e.message}")
        }
    }

    private fun getDownloadDirectory(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DOWNLOAD_DIR
        )
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(Regex("[<>:\"|?*]"), "_")
            .take(255)
    }

    fun ensureUniqueFileName(file: File): File {
        if (!file.exists()) return file

        val name = file.nameWithoutExtension
        val ext = file.extension
        var counter = 1
        var newFile: File

        do {
            newFile = File(file.parentFile, "$name($counter).$ext")
            counter++
        } while (newFile.exists())

        return newFile
    }
}
