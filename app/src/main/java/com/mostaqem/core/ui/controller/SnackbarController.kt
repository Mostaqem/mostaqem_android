package com.mostaqem.core.ui.controller

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarAction(
    val name: String,
    val action: () -> Unit
)

data class SnackbarEvents(
    val message: String,
    val action: SnackbarAction? = null
)

object SnackbarController {

    private val _events = Channel<SnackbarEvents>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(events: SnackbarEvents) {
        _events.send(events)
    }

}