package com.mostaqem.features.personalization.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.dataStore
import com.mostaqem.features.personalization.presentation.reciter.ReciterOption
import com.mostaqem.features.player.domain.MaterialShapes
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.presentation.ReciterScreen
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.personalization.presentation.shapes.OptionShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier, navController: NavController, playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val userSettings = context.dataStore.data.collectAsState(initial = AppSettings()).value



    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        LargeTopAppBar(
            title = { Text(text = "السمات") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
        )
        ListItem(headlineContent = {
            Text("الشيخ المفضل", Modifier.padding(horizontal = 18.dp))
        },
            supportingContent = {
                Text(
                    "تغير الشيخ المفضل لديك و يمكنك الضغط طويلا لتغير التلاوة المفضلة لديك",
                    Modifier.padding(horizontal = 18.dp)
                )
            }
        )
        Spacer(Modifier.height(15.dp))

        ReciterOption(playerViewModel = playerViewModel)

        Spacer(Modifier.height(15.dp))

        Text("تغيير شكل عرض الصورة في المشغل", Modifier.padding(horizontal = 18.dp))
        Spacer(Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            contentPadding = WindowInsets.safeContent.asPaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(MaterialShapes.entries) {
                OptionShape(
                    isSelected = userSettings.shapeID == it.id,
                    materialShape = it,
                    context = context
                )
            }
        }
        Spacer(Modifier.height(100.dp))
    }

}

