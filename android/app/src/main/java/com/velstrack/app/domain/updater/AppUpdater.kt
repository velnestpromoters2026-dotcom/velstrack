package com.velstrack.app.domain.updater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.velstrack.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var downloadId: Long = -1

    suspend fun checkForUpdates(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/velnestpromoters2026-dotcom/velstrack/releases/latest")
            val response = url.readText()
            val json = JSONObject(response)
            val tagName = json.optString("tag_name", "").replace("v", "")
            
            val currentVersion = BuildConfig.VERSION_NAME.replace("v", "")
            
            // Basic string compare (assuming version scheme like 1.0, 1.1)
            if (tagName.isNotEmpty() && tagName != currentVersion) {
                val assets = json.optJSONArray("assets")
                if (assets != null && assets.length() > 0) {
                    val downloadUrl = assets.getJSONObject(0).optString("browser_download_url")
                    if (downloadUrl.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            startDownload(downloadUrl, tagName)
                        }
                        return@withContext true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AppUpdater", "Failed to check for updates", e)
        }
        return@withContext false
    }

    private fun startDownload(url: String, version: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Velstrack Update v$version")
            .setDescription("Downloading latest version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "velstrack_update.apk")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "velstrack_update.apk")
        if (file.exists()) {
            file.delete()
        }

        downloadId = manager.enqueue(request)
        
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    installApk(file)
                    try {
                        context.unregisterReceiver(this)
                    } catch (e: Exception) {}
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun installApk(file: File) {
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e("AppUpdater", "Failed to install APK", e)
        }
    }
}
