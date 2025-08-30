package com.mostaqem.features.surahs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.materialkolor.ktx.harmonize
import com.mostaqem.R
import com.mostaqem.features.surahs.data.Surah

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SurahListItem(
    modifier: Modifier = Modifier,
    surah: Surah,
    isArabic: Boolean,
    defaultReciterName: String,
    isCurrentSurahPlayed: Boolean,
    polygon: RoundedPolygon = MaterialShapes.Flower,
    onMenu: () -> Unit,
    onClick: () -> Unit,
) {
    // TODO("Refactor This")

    ListItem(
        headlineContent = {
            Text(text = if (isArabic) surah.arabicName else surah.complexName)
        },
        supportingContent = { Text(text = defaultReciterName) },
        leadingContent = {

            Box(contentAlignment = Alignment.Center) {
                if (isCurrentSurahPlayed) {
                    LoadingIndicator(
                        color = MaterialTheme.colorScheme.onSecondaryContainer.harmonize(
                            Color(
                                0xffFFA500
                            )
                        ),
                        polygons = listOf(MaterialShapes.Flower, MaterialShapes.Circle)
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(MaterialShapes.Square.toShape())
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(25.dp)
                                .clip(polygon.toShape())
                                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer.harmonize(Color(0xffFFA500)),
            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer.harmonize(
                Color(
                    0xffFFA500
                )
            ),
            supportingColor = MaterialTheme.colorScheme.onSecondaryContainer.harmonize(
                Color(
                    0xffFFA500
                )
            ),
            trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.harmonize(
                Color(
                    0xffFFA500
                )
            ),

            ) else ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier
            .clip(RoundedCornerShape(28))
            .clickable {
                onClick()
            }
    )
}
