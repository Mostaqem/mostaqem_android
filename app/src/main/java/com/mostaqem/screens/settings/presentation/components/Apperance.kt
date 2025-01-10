package com.mostaqem.screens.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.dataStore
import com.mostaqem.screens.player.domain.MaterialShapes
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.settings.domain.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier, navController: NavController, playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val customShapeData = context.dataStore.data.collectAsState(initial = AppSettings()).value

    Column(modifier = modifier) {
        LargeTopAppBar(
            title = { Text(text = "السمات") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
        )
        Text("تغيير شكل عرض الصورة في التشغيل", Modifier.padding(horizontal = 18.dp))
        Spacer(Modifier.height(15.dp))
        LazyRow(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            items(MaterialShapes.entries) {
                OptionShape(
                    isSelected = customShapeData.shapeID == it.id,
                    materialShape = it,
                    playerViewModel = playerViewModel,
                    context = context
                )
            }
        }
    }

}

