package com.mostaqem.screens.player.domain

import android.graphics.Matrix
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import kotlinx.serialization.Serializable

class CustomShape(private val shapeType: ShapeType) : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(path = PathParser.createPathFromPathData(
            shapeType.pathData
        ).asComposePath().apply {
            val pathSize = getBounds().size
            val matrix = Matrix()
            matrix.postScale(
                size.width / pathSize.width, size.height / pathSize.height
            )
            asAndroidPath().transform(matrix)
            val left = getBounds().left
            val top = getBounds().top
            translate(Offset(-left, -top))
        })
    }

}

enum class MaterialShapes(val id: String, val shape: Shape) {
    RECT("rect", RoundedCornerShape(16)),
    OCTAGON("octagon", CustomShape(Octagon())),
    CIRCLE("circle", CircleShape),

}