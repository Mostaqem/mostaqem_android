package com.mostaqem.features.settings.data

data class Options(
    val name: String,
    val description: String,
    val icon: Int,
    val onClick: () -> Unit
)
