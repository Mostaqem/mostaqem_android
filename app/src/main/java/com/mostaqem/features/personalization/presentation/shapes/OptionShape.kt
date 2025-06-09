package com.mostaqem.features.personalization.presentation.shapes

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.features.personalization.presentation.PersonalizationViewModel
import com.mostaqem.features.player.domain.MaterialShapes
import com.mostaqem.features.player.presentation.PlayerViewModel


@Composable
fun OptionShape(
    materialShape: MaterialShapes,
    isSelected: Boolean,
    personalizationViewModel: PersonalizationViewModel = hiltViewModel(),
) {
    Box(contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .clip(materialShape.shape)
            .background(MaterialTheme.colorScheme.primary)
            .size(100.dp)
            .clickable {
                personalizationViewModel.changeShape(materialShape.id)
            })
        if (isSelected) Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
                .size(20.dp)
        )
    }
}