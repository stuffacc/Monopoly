package org.example.project.data

import org.example.project.UI.screen.game.GameState
import org.example.project.data.GameChangeApplier.applyGameChanges
import org.example.project.data.models.game.*

object GameEngine {
    fun handle(gameState: GameState, gameAction: GameAction): GameState {
        val gameChanges = GameChangeGenerator.processAction(gameState, gameAction)

        return applyGameChanges(gameState, gameChanges)
    }

    fun getAvailableActions(gameState: GameState, cellId: Int): List<GameAction> {
        val availableActionList = mutableListOf<GameAction>()

        // TODO сделать место, где будут генерироапться кубики, Без параметров сделать для UI
        val dice1 = (1..6).random()
        val dice2 = (1..6).random()

        if (GameActionValidator.isActionAvailable(gameState, ThrowDiceAction(dice1, dice2))) {
            availableActionList.add(ThrowDiceAction(dice1, dice2))
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

        if (GameActionValidator.isActionAvailable(gameState, EndTurnAction())) {
            availableActionList.add(EndTurnAction())
        }

        return availableActionList
    }
}