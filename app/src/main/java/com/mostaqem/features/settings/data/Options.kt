package com.mostaqem.features.settings.data

import androidx.compose.ui.graphics.vector.ImageVector

data class Options(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
