package org.example.project.data.models.player

import androidx.compose.ui.graphics.Color

data class Player(
    val id: String,
    val name: String,
    val color: Color,
    val position: Int = 0,
    val playerState: PlayerState = PlayerState.IN_GAME,
    val balance: Int = 1500,
    val turnsInJail: Int = 0,
    val doubleCount: Int = 0
)