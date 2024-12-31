package com.mostaqem.screens.settings.data

data class Options(
    val name: String,
    val description: String,
    val icon: Int,
    val onClick: () -> Unit
)
