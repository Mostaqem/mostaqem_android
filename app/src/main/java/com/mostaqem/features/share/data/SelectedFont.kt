package com.mostaqem.features.share.data

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.TextUnit

data class SelectedFont(
    val name: String,
    val font: Font,
    val lineHeight: TextUnit,
    val fontSize: TextUnit
)
