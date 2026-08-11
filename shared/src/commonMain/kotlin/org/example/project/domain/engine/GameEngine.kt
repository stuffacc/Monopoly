package org.example.project.domain.engine

import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.ThrowDiceAction

object GameEngine {
    fun handle(gameState: GameState, gameAction: GameAction): GameState {
        val gameChanges = GameChangeGenerator.processAction(gameState, gameAction)

        return GameChangeApplier.applyGameChanges(gameState, gameChanges)
    }

    fun getAvailableActions(gameState: GameState, cellId: Int): List<GameAction> {
        val availableActionList = mutableListOf<GameAction>()

        // TODO: Можно так оставить, всё равно значения на кубике не влияют на доступность кинуть кубики
        // Или отдельные для UI сделать доступные действия, но тогда все действия, кроме бросания кубиков, будут одинаковы
        if (GameActionValidator.isActionAvailable(gameState, ThrowDiceAction(1, 0))) {
            availableActionList.add(ThrowDiceAction(1, 0))
        }

        if (GameActionValidator.isActionAvailable(gameState, BuyPropertyAction(cellId))) {
            availableActionList.add(BuyPropertyAction(cellId))
        }

        if (GameActionValidator.isActionAvailable(gameState, BuyUpgradeAction(cellId))) {
            availableActionList.add(BuyUpgradeAction(cellId))
        }

        if (GameActionValidator.isActionAvailable(gameState, SellUpgradeAction(cellId))) {
            availableActionList.add(SellUpgradeAction(cellId))
        }

        if (GameActionValidator.isActionAvailable(gameState, EndTurnAction)) {
            availableActionList.add(EndTurnAction)
        }

        return availableActionList
    }
}