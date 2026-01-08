package com.mostaqem.core.ui.app

import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.mostaqem.core.navigation.models.TOP_LEVEL_DESTINATIONS


@Composable
fun AppNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {


    BottomAppBar(
        modifier = modifier
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (destination, data) ->
            NavigationBarItem(
                selected = destination == selectedKey,
                onClick = { onSelectKey(destination) },
                label = { Text(stringResource(data.title)) },
                icon = {
                    if (destination == selectedKey) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = data.selectedIcon),
                            modifier = Modifier.size(22.dp),
                            contentDescription = "selected"
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = data.icon),
                            modifier = Modifier.size(22.dp),
                            contentDescription = "nonselected"
                        )
                    }
                },
            )
        }

    }
}