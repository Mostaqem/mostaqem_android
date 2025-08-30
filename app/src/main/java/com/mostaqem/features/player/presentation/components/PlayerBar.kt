package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    playerIcon: Int,
    onPlayPause: () -> Unit,
    surahName: String,
    reciterName: String,
    progress: Float,
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(MaterialShapes.Square.toShape())
                            .background(MaterialTheme.colorScheme.primary)
                    ){
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .clip(MaterialShapes.Flower.toShape())
                                .background(MaterialTheme.colorScheme.onPrimary)
                        )
                    }

                Spacer(modifier = Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = surahName,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                    Text(
                        text = reciterName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = {
                onPlayPause()
            }) {
                Icon(
                    painter = painterResource(id = playerIcon),
                    contentDescription = "play",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        val progressAnimation by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(easing = FastOutSlowInEasing), label = "progress",
        )
        LinearProgressIndicator(
            progress = { progressAnimation },
            trackColor = MaterialTheme.colorScheme.surfaceContainer,

            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}





