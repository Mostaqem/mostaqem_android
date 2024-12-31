package com.mostaqem.screens.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.screens.settings.domain.ApkHandle
import com.mostaqem.screens.settings.domain.GithubAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val githubAPI: GithubAPI,
    private val apkHandle: ApkHandle
) : ViewModel() {
    private val _showUpdatePrompt = MutableStateFlow<Boolean>(false)
    val showUpgradePrompt: StateFlow<Boolean> = _showUpdatePrompt

    fun checkForUpgrade(currentVersion: String) {
        viewModelScope.launch {
            val latestRelease = githubAPI.getLatestRelease("Mostaqem", "mostaqem_android")
            if (isNewVersionAvailable(latestRelease.tagName)) {
                _showUpdatePrompt.value = true
            }
        }
    }

    fun performUpgrade(context: Context, apkUrl: String) {
        viewModelScope.launch {
            val apkFile = apkHandle.downloadApk(apkUrl, context)
            apkHandle.installApk(context, apkFile)
        }
    }

    private fun isNewVersionAvailable(latestVersion: String): Boolean {
//        val currentVersion = BuildConfig //TODO: GetCurrentVersion
        return latestVersion > currentVersion
    }


}