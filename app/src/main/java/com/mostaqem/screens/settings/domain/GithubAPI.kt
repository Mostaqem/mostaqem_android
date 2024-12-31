package com.mostaqem.screens.settings.domain

import com.mostaqem.screens.settings.data.Release
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubAPI {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(@Path("owner") owner: String, @Path("repo") repo: String): Release
}