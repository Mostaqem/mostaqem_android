package com.mostaqem.features.update.domain

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val githubAPI: GithubAPI,
    private val apkHandle: ApkHandle,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val latestRelease = githubAPI.getLatestRelease("Mostaqem", "mostaqem_android")
                val currentVersion = getAppVersionName(context)

                if (isNewVersionAvailable(currentVersion, latestRelease.tagName)) {
                    val apkUrl = latestRelease.assets.first().browserDownloadUrl
                    apkHandle.downloadApk(context,apkUrl, allowInstall = false)
                    Result.success()
                } else {
                    Result.success()
                }
            } catch (e: Exception) {
                Log.e("UpdateCheckWorker", "Error: ${e.message}")
                Result.failure()
            }
        }
    }

    private fun getAppVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName.toString()
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun isNewVersionAvailable(currentVersion: String, latestVersion: String): Boolean {
        return latestVersion > currentVersion
    }

}