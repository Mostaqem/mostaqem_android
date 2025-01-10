package com.mostaqem.screens.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val shapeID: String = "rect"
)
