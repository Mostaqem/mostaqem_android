package com.mostaqem.screens.settings.domain

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class ApkHandle(private val context: Context) {
    fun installApk(context: Context, apkFile: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                FileProvider.getUriForFile(
                    context, "${context.packageName}.provider", apkFile
                ), "application/vnd.android.package-archive"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    suspend fun downloadApk(url: String, context: Context): File {
        val file = File(context.getExternalFilesDir(null), "update.apk")
        val response = withContext(Dispatchers.IO) {
            OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        }
        if (response.isSuccessful) {
            response.body?.byteStream()?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }
}