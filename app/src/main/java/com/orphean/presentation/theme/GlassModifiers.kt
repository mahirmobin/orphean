package com.orphean.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.glassmorphism(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    intensity: Float = 1f
) = composed {
    val isDark = isSystemInDarkTheme()
    val glassColor = if (isDark) {
        Color.White.copy(alpha = 0.05f * intensity)
    } else {
        Color.White.copy(alpha = 0.5f * intensity)
    }
    
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.0f),
            Color.White.copy(alpha = 0.1f)
        )
    )

    this.then(
        Modifier
            .clip(shape)
            .background(glassColor)
            .border(borderWidth, borderBrush, shape)
    )
}
