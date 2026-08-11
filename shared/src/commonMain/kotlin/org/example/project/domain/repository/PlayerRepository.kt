package org.example.project.domain.repository

import org.example.project.domain.models.player.Player
import org.example.project.domain.models.player.SendPlayer

interface PlayerRepository {
    fun createPlayersForGame(sendPlayers: List<SendPlayer>): List<Player>
}