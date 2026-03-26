package com.confecciones.esperanza.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
}

object AppRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 20.dp
    val xl = 28.dp
}

object AppElevation {
    val sm = 2.dp
    val md = 6.dp
    val lg = 12.dp
}

object AppGradients {
    val primary = Brush.verticalGradient(
        colors = listOf(PurplePrimary, PinkAccent)
    )

    val header = Brush.horizontalGradient(
        colors = listOf(PurplePrimary, RoseAccent)
    )
}
