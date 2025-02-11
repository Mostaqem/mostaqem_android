package com.mostaqem.screens.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.dataStore
import com.mostaqem.screens.player.domain.MaterialShapes
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.reciters.presentation.ReciterScreen
import com.mostaqem.screens.settings.domain.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier, navController: NavController, playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val userSettings = context.dataStore.data.collectAsState(initial = AppSettings()).value
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showReciters = remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        LargeTopAppBar(
            title = { Text(text = "السمات") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
        )
        Text("الشيخ المفضل", Modifier.padding(horizontal = 18.dp))
        Spacer(Modifier.height(15.dp))
        if (showReciters.value) {
            ModalBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = {
                    showReciters.value = false
                }) {
                ReciterScreen(
                    isDefaultBtn = true,
                    playerViewModel = playerViewModel,
                    bottomSheet = showReciters
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable {
                    playerViewModel.changeReciter(userSettings.reciterSaved)
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = userSettings.reciterSaved.image,
                        contentDescription = "reciter",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .size(60.dp)

                    )
                    Spacer(Modifier.width(15.dp))
                    Text(userSettings.reciterSaved.arabicName)
                }
                IconButton(onClick = {
                    showReciters.value = true
                }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "edit")
                }

            }
        }
        Spacer(Modifier.height(15.dp))

        Text("تغيير شكل عرض الصورة في المشغل", Modifier.padding(horizontal = 18.dp))

        Spacer(Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            contentPadding = WindowInsets.safeContent.asPaddingValues(),
            modifier = Modifier
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {
            items(MaterialShapes.entries) {
                OptionShape(
                    isSelected = userSettings.shapeID == it.id,
                    materialShape = it,
                    playerViewModel = playerViewModel,
                    context = context
                )
            }
        }
    }

}

