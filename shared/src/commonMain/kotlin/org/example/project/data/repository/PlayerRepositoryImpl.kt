package org.example.project.data.repository

import org.example.project.domain.models.colors
import org.example.project.domain.models.player.Player
import org.example.project.domain.models.player.SendPlayer
import org.example.project.domain.repository.PlayerRepository
import kotlin.uuid.Uuid

class PlayerRepositoryImpl: PlayerRepository {
    override fun createPlayersForGame(sendPlayers: List<SendPlayer>): List<Player> {
        val players = mutableListOf<Player>()

        for (i in sendPlayers.indices) {
            val id = Uuid.random().toString()
            players.add(
                Player(
                    id = id,
                    name = sendPlayers[i].name,
                    color = colors[i]
                )
            )
        }

        return players
    }
}