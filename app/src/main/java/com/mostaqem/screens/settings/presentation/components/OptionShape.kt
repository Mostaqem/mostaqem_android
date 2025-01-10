package com.mostaqem.screens.settings.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mostaqem.screens.player.domain.MaterialShapes
import com.mostaqem.screens.player.presentation.PlayerViewModel


@Composable
fun OptionShape(materialShape: MaterialShapes, isSelected: Boolean, playerViewModel: PlayerViewModel, context: Context) {
    Box(contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .clip(materialShape.shape)
            .background(MaterialTheme.colorScheme.primary)
            .size(100.dp)
            .clickable {
                playerViewModel.changeShape(materialShape.id,context)
            })
        if (isSelected) Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
                .size(20.dp)
        )
    }
}