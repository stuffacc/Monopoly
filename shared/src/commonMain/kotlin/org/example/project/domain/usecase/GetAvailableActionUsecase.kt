package org.example.project.domain.usecase

import org.example.project.domain.engine.GameEngine
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState

class GetAvailableActionUsecase {
    fun execute(gameState: GameState, cellId: Int): List<GameAction> {
        return GameEngine.getAvailableActions(gameState, cellId)
    }
}