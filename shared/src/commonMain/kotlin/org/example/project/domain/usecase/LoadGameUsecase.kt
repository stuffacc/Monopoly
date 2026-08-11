package org.example.project.domain.usecase

import org.example.project.domain.models.game.GameState
import org.example.project.domain.repository.GameRepository

class LoadGameUsecase(private val gameRepository: GameRepository) {
    fun execute(gameId: String): GameState {
        return gameRepository.getGameById(gameId)
    }
}