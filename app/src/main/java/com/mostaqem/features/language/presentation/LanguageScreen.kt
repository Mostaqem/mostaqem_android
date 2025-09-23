package com.mostaqem.features.language.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.language.domain.AppLanguages
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.settings.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LanguageScreen(
    navController: NavController,
    viewModel: LanguageViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val selectedLanguageCode =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val isPlaying = playerViewModel.playerState.value.surah != null

    LazyColumn {
        item {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.language), fontFamily = MaterialTheme.typography.headlineLarge.fontFamily)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
            )
        }
        if (isPlaying) {
            item {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Outlined.Warning, contentDescription = "warning")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.stop_player_language))
                    }
                }
            }
        }

        items(AppLanguages.entries) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = it.displayLanguage,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = if (it.rtl) TextAlign.End else TextAlign.Start,
                            fontWeight = if (selectedLanguageCode == it.code) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedLanguageCode == it.code) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    },
                    trailingContent = {
                        if (!it.rtl) RadioButton(
                            selected = it.code == selectedLanguageCode,
                            onClick = {
                                viewModel.changeLanguage(it.code)
                                playerViewModel.clear()
                            })
                    },
                    leadingContent = {
                        if (it.rtl) RadioButton(
                            selected = it.code == selectedLanguageCode,
                            onClick = {
                                viewModel.changeLanguage(it.code)
                                playerViewModel.clear()
                            })
                    },


                    modifier = Modifier
                        .clickable {
                            viewModel.changeLanguage(it.code)
                            playerViewModel.clear()

                        }
                )
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}


