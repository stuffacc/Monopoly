package org.example.project.ui.screen.enter

import org.example.project.domain.models.player.SendPlayer

data class EnterScreenState(
    val name: String = "",
    val sendPlayers: List<SendPlayer> = emptyList(),
    val gameId: String = "0"
)