package org.example.project.domain.usecase

import org.example.project.domain.repository.GameRepository
import org.example.project.domain.repository.PlayerRepository
import org.example.project.domain.models.player.SendPlayer

class CreateGameUsecase(private val gameRepository: GameRepository, private val playerRepository: PlayerRepository) {
    fun execute(sendPlayers: List<SendPlayer>): String {
        val players = playerRepository.createPlayersForGame(sendPlayers)

        return gameRepository.createGame(players)
    }
}