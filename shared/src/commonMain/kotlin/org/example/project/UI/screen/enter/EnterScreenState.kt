package org.example.project.UI.screen.enter

import org.example.project.domain.models.player.Player

data class EnterScreenState(
    val name: String = "",
    val players: List<Player> = emptyList(),
    val gameId: String = "0"
)