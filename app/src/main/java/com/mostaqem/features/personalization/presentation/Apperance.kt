package com.mostaqem.features.personalization.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.ShapesDestination
import com.mostaqem.features.personalization.presentation.components.LargeTopBar
import com.mostaqem.features.player.presentation.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceScreen(
    modifier: Modifier = Modifier, navController: NavController, playerViewModel: PlayerViewModel
) {

    val fontFamily = MaterialTheme.typography.titleLarge.fontFamily!!

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        LargeTopBar(navController, title = stringResource(R.string.apperanace))

        ListItem(
            modifier = Modifier.clickable {
                navController.navigate(ShapesDestination)
            },
            headlineContent = {
                Text(
                    stringResource(R.string.change_shape),
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            },
            trailingContent = {

                Icon(Icons.AutoMirrored.Filled.ArrowForward, "forward")

            })

    }

}

