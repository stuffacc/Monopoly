package org.example.project.domain.usecase

import org.example.project.data.RandomValueGenerator
import org.example.project.domain.engine.GameEngine
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.ThrowDiceAction

class SendActionUsecase(private val randomValueGenerator: RandomValueGenerator) {
    fun execute(gameState: GameState, gameAction: GameAction): GameState {
        if (gameAction is ThrowDiceAction) {
            val dice1 = randomValueGenerator.generate()
            val dice2 = randomValueGenerator.generate()

            return GameEngine.handle(
                gameState, ThrowDiceAction(
                    dice1 = dice1,
                    dice2 = dice2
                )
            )
        }

        return GameEngine.handle(gameState, gameAction)
    }

}