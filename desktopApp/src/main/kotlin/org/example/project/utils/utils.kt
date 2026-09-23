package org.example.project.utils

import androidx.compose.ui.graphics.Color as ComposeColor
import org.example.project.domain.models.Color

fun Color.toComposeColor(): ComposeColor {
    return when (this) {
        Color.BROWN -> ComposeColor(0xFF8B5A2B)
        Color.YELLOW -> ComposeColor(0xFFF4D03F)
        Color.BLUE -> ComposeColor(0xFF2E86C1)
        Color.DARK_BLUE -> ComposeColor(0xFF0B3D91)
        Color.VIOLET -> ComposeColor(0xFF6F2DBD)
        Color.ORANGE -> ComposeColor(0xFFEA7A1A)
        Color.RED -> ComposeColor(0xFFE53935)
        Color.GREEN -> ComposeColor(0xFF2E7D32)
    }
}