package com.mostaqem.screens.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.screens.settings.domain.ApkHandle
import com.mostaqem.screens.settings.domain.GithubAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val githubAPI: GithubAPI,
    private val apkHandle: ApkHandle,

    ) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }
    private val _showUpdatePrompt = MutableStateFlow<Boolean>(false)
    val showUpgradePrompt: StateFlow<Boolean> = _showUpdatePrompt

    private val _newVersion = MutableStateFlow<String?>(null)
    val newVersion: StateFlow<String?> = _newVersion
    var apkInstallURL: String? = null

    fun checkForUpgrade(currentVersion: String) {
        viewModelScope.launch(errorHandler) {
            val latestRelease = githubAPI.getLatestRelease("Mostaqem", "mostaqem_android")
            if (isNewVersionAvailable(latestRelease.tagName, currentVersion)) {
                _showUpdatePrompt.value = true
                _newVersion.value = latestRelease.tagName
                apkInstallURL = latestRelease.assets[0].browserDownloadUrl

            }
        }
    }

    fun performUpgrade(context: Context) {
        viewModelScope.launch(errorHandler) {
            if (apkInstallURL != null) {
                val apkFile = apkHandle.downloadApk(apkInstallURL!!, context)
                apkHandle.installApk(context, apkFile)

            }

        }
    }

    private fun isNewVersionAvailable(latestVersion: String, currentVersion: String): Boolean {
        return latestVersion > currentVersion
    }

}