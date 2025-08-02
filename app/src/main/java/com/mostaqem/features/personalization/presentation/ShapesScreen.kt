package com.mostaqem.features.personalization.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.features.personalization.presentation.components.shapes.OptionShape
import com.mostaqem.features.player.domain.AppShapes

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShapesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PersonalizationViewModel,

    ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val appSettings by viewModel.userSettings.collectAsState()
    val shapeID = appSettings.shapeID
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Shapes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                    }
                },
            )

        }) { paddingValues ->
        LazyVerticalGrid(
            contentPadding = paddingValues,
            modifier = Modifier.padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            columns = GridCells.Adaptive(200.dp),

            ) {
            items(AppShapes.entries, key = { it.id }) {
                val isSelected = shapeID == it.id
                val animatedColor by
                animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                    animationSpec = tween(durationMillis = 300)
                )
                val animateSize by animateIntAsState(
                    targetValue = if (isSelected) 150 else 125,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                )

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedColor)
                        .padding(10.dp)
                        .noRippleClickable {
                            viewModel.changeShape(it.id)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    OptionShape(
                        isSelected = isSelected,
                        materialShape = it,
                        size = animateSize.dp
                    )
                }

            }
            item {
                Spacer(Modifier.height(100.dp))
            }
        }

    }

}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
