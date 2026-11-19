package com.ultrahd.downloader

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var engineReady = false

    private lateinit var urlInput: EditText
    private lateinit var qualityGroup: RadioGroup
    private lateinit var formatGroup: RadioGroup
    private lateinit var downloadBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.urlInput)
        qualityGroup = findViewById(R.id.qualityGroup)
        formatGroup = findViewById(R.id.formatGroup)
        downloadBtn = findViewById(R.id.downloadBtn)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)

        requestLegacyStoragePermissionIfNeeded()
        initDownloadEngine()

        downloadBtn.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isEmpty() || (!url.contains("youtube.com") && !url.contains("youtu.be"))) {
                statusText.text = "দয়া করে একটি সঠিক YouTube লিংক দিন।"
                return@setOnClickListener
            }
            if (!engineReady) {
                statusText.text = "ইঞ্জিন প্রস্তুত হচ্ছে, কয়েক সেকেন্ড অপেক্ষা করুন..."
                return@setOnClickListener
            }
            startDownload(url)
        }
    }

    private fun requestLegacyStoragePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1001
                )
            }
        }
    }

    private fun initDownloadEngine() {
        downloadBtn.isEnabled = false
        statusText.text = "yt-dlp ও FFmpeg ইঞ্জিন চালু হচ্ছে..."

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    YoutubeDL.getInstance().init(application)
                    FFmpeg.getInstance().init(application)
                }
            }
            if (result.isSuccess) {
                engineReady = true
                downloadBtn.isEnabled = true
                statusText.text = "প্রস্তুত! YouTube লিংক পেস্ট করে 8K/4K/2K বেছে নিন।"
            } else {
                statusText.text = "ইঞ্জিন চালু করতে সমস্যা: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    private fun startDownload(url: String) {
        val selectedQuality = when (qualityGroup.checkedRadioButtonId) {
            R.id.radio8k -> "4320"
            R.id.radio4k -> "2160"
            R.id.radio2k -> "1440"
            R.id.radio1080 -> "1080"
            R.id.radioAudio -> "audio"
            else -> "2160"
        }

        val container = if (formatGroup.checkedRadioButtonId == R.id.radioMp4) "mp4" else "mkv"

        val outputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "UltraHD-YouTube"
        )
        if (!outputDir.exists()) outputDir.mkdirs()

        downloadBtn.isEnabled = false
        progressBar.progress = 0
        statusText.text = "ডাউনলোড শুরু হচ্ছে..."

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val request = YoutubeDLRequest(url)
                    request.addOption("--no-playlist")
                    request.addOption("-o", "${outputDir.absolutePath}/%(title).100s [%(height)sp].%(ext)s")

                    if (selectedQuality == "audio") {
                        request.addOption("-f", "bestaudio/best")
                        request.addOption("-x")
                        request.addOption("--audio-format", "mp3")
                        request.addOption("--audio-quality", "0")
                    } else {
                        request.addOption(
                            "-f",
                            "bestvideo[height<=$selectedQuality]+bestaudio/best[height<=$selectedQuality]/best"
                        )
                        request.addOption("--merge-output-format", container)
                    }

                    YoutubeDL.getInstance().execute(request, "ultrahd_dl") { progress, etaInSeconds, line ->
                        runOnUiThread {
                            if (progress >= 0f) {
                                progressBar.progress = progress.toInt().coerceIn(0, 100)
                                statusText.text = "ডাউনলোড: ${"%.1f".format(progress)}% (বাকি ~${etaInSeconds} সে.)"
                            } else if (line.contains("Merger", ignoreCase = true) || line.contains("ffmpeg", ignoreCase = true)) {
                                progressBar.progress = 99
                                statusText.text = "FFmpeg দিয়ে অডিও ও ভিডিও মার্জ করা হচ্ছে..."
                            }
                        }
                    }
                }
            }

            downloadBtn.isEnabled = true
            if (result.isSuccess) {
                progressBar.progress = 100
                statusText.text = "সম্পন্ন! সেভ হয়েছে: Download/UltraHD-YouTube ফোল্ডারে।"
                outputDir.listFiles()?.forEach { file ->
                    MediaScannerConnection.scanFile(this@MainActivity, arrayOf(file.absolutePath), null, null)
                }
            } else {
                statusText.text = "ত্রুটি হয়েছে: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}
