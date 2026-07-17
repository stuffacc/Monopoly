package org.example.project.data.models.player

import androidx.compose.ui.graphics.Color

data class Player(
    val id: String,
    val name: String,
    val color: Color,
    var position: Int = 0,
    var playerState: PlayerState = PlayerState.IN_GAME,
    var balance: Int = 1500,
    var turnsInJail: Int = 0,
    var doubleCount: Int = 0
)