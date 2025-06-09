package com.mostaqem.features.surahs.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.features.surahs.data.Surah
import kotlin.math.max
import android.graphics.Path as AndroidPath

@Composable
fun SurahListItem(
    modifier: Modifier = Modifier,
    surah: Surah,
    isArabic: Boolean,
    isCurrentSurahPlayed: Boolean,
    onMenu: () -> Unit,
    onClick: () -> Unit,
) {

    ListItem(
        headlineContent = {
            Text(text = if (isArabic) surah.arabicName else surah.complexName)
        },
        supportingContent = { Text(text = if (isArabic) surah.complexName else surah.arabicName) },
        leadingContent = {

            Box(contentAlignment = Alignment.Center) {
                if (isCurrentSurahPlayed) {

                    val starPolygon = remember {
                        RoundedPolygon.star(
                            numVerticesPerRadius = 12,
                            innerRadius = 1f / 3f,
                            rounding = CornerRounding(1f / 6f)
                        )
                    }
                    val circlePolygon = remember {
                        RoundedPolygon.circle(
                            numVertices = 12
                        )
                    }
                    val morph = remember { Morph(starPolygon, circlePolygon) }
                    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
                    val progress = infiniteTransition.animateFloat(
                        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                            tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "progress"
                    )
                    val rotation = infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "rotation"
                    )
                    var morphPath = remember { Path() }
                    var androidPath = remember { AndroidPath() }
                    var matrix = remember { Matrix() }
                    val color = MaterialTheme.colorScheme.onTertiaryContainer
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .drawWithCache {
                                androidPath = morph.toPath(progress.value, androidPath)
                                morphPath = androidPath.asComposePath()
                                matrix.reset()
                                matrix.scale(size.minDimension / 1.7f, size.minDimension / 1.7f)
                                morphPath.transform(matrix)

                                onDrawBehind {
                                    rotate(rotation.value) {
                                        translate(size.width / 2f, size.height / 2f) {
                                            drawPath(
                                                morphPath,
                                                color = color,
                                                style = Fill
                                            )
                                        }
                                    }

                                }
                            })
                } else {

                    AsyncImage(
                        model = surah.image,
                        contentDescription = "surah",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(18))

                    )
                }
            }

        },
        trailingContent = {
            IconButton(onClick = {
                onMenu()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_more_vert_24),
                    contentDescription = "play"
                )
            }

        },
        colors = if (isCurrentSurahPlayed) ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
            supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
            trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,

            ) else ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier
            .clip(RoundedCornerShape(28))
            .clickable {
                onClick()
            }
    )
}
