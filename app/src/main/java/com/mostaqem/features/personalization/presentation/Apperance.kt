package com.mostaqem.features.personalization.presentation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.personalization.presentation.reciter.ReciterOption
import com.mostaqem.features.player.domain.MaterialShapes
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.personalization.presentation.shapes.OptionShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier, navController: NavController, playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val userSettings = context.dataStore.data.collectAsState(initial = AppSettings()).value

    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        LargeTopAppBar(
            title = { Text(text = stringResource(R.string.apperanace), fontFamily = fontFamily) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
        )
        ListItem(
            headlineContent = {
                Text(
                    stringResource(R.string.default_reciter),
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            },
            supportingContent = {
                Text(
                    stringResource(R.string.change_default_reciter),
                    Modifier.padding(horizontal = 18.dp)
                )
            }
        )
        Spacer(Modifier.height(15.dp))

        ReciterOption(playerViewModel = playerViewModel)

        Spacer(Modifier.height(15.dp))

        Text(
            stringResource(R.string.change_shape),
            fontFamily = fontFamily,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        Spacer(Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
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
                )
            }
        }
        Spacer(Modifier.height(100.dp))
    }

}

