package org.example.project.domain

import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.player.Player

interface GameRepository {
    fun createGame(players: List<Player>): String

    fun getGameById(id: String): GameState
}