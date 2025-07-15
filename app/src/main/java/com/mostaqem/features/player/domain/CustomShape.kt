@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.mostaqem.features.player.domain

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon


enum class AppShapes(val id: String, val shape: RoundedPolygon) {
    RECT("rect", MaterialShapes.Square),
    COOKIE12("cookie_12", MaterialShapes.Cookie12Sided),
    CIRCLE("circle", MaterialShapes.Circle),
    BUN("bun", MaterialShapes.Bun),
    ARROW("arrow", MaterialShapes.Arrow),
    TRIANGLE("triangle", MaterialShapes.Triangle),
    ARCH("arch", MaterialShapes.Arch),
    COOKIE4("cookie_4", MaterialShapes.Cookie4Sided),
    COOKIE6("cookie_6", MaterialShapes.Cookie6Sided),
    LEAF("leaf_4", MaterialShapes.Clover4Leaf),
    DIAMOND("diamond", MaterialShapes.Diamond),
    GEM("gem", MaterialShapes.Gem),


}
