package org.example.project.UI.screen.enter

import org.example.project.data.models.player.Player

data class EnterState(
    val name: String = "",
    val players: List<Player> = emptyList(),
    val gameId: String = "0"
)