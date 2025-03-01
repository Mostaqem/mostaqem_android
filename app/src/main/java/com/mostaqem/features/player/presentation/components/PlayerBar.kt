package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mostaqem.features.player.domain.MaterialShapes

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    image: Any,
    playerIcon: Int,
    onPlayPause: () -> Unit,
    surahName: String,
    reciterName: String,
    progress: Float,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row {
                with(sharedTransitionScope) {
                    AsyncImage(
                        model = image,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .size(50.dp)
                            .clip(
                                MaterialShapes.RECT.shape
                            )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = surahName,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = reciterName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
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

//            IconButton(
//                onClick = {},
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.outline_skip_previous_24),
//                    contentDescription = "play",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            }



        }
        val progressAnimation by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(easing = FastOutSlowInEasing), label = "progress",
        )
        LinearProgressIndicator(
            progress = { progressAnimation },
            trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}





