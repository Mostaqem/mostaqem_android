package com.mostaqem.features.personalization.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.glance.layout.RowScope
import androidx.navigation.NavController
import com.mostaqem.core.ui.app.Navigator

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun LargeTopBar(
    navController: Navigator,
    title: String,
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {},
) {
    val fontFamily = MaterialTheme.typography.titleLarge.fontFamily!!

    LargeFlexibleTopAppBar(
        title = { Text(text = title, fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = { navController.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
            }
        },
        actions = actions
    )
}

