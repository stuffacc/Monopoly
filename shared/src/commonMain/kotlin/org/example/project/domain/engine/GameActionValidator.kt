package org.example.project.domain.engine

import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameTurnPhase
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.PlayerState
import kotlin.math.max
import kotlin.math.min

object GameActionValidator {
    fun isActionAvailable(gameState: GameState, gameAction: GameAction): Boolean {
        return when (gameAction) {
            is ThrowDiceAction -> isThrowDiceActionAvailable(gameState, gameAction)
            is BuyPropertyAction -> isBuyPropertyActionAvailable(gameState, gameAction)
            is BuyUpgradeAction -> isBuyUpgradeActionAvailable(gameState, gameAction)
            is SellUpgradeAction -> isSellUpgradeActionAvailable(gameState, gameAction)
            is EndTurnAction -> isEndGameActionAvailable(gameState, gameAction)
        }
    }

    private fun getCellsSameColor(cells: List<Cell>, streetCell: StreetCell): List<StreetCell> {
        val cellsSameColor = mutableListOf<StreetCell>()

        for (cell in cells) {
            if ((cell is StreetCell) && (cell.propertyStreet.streetColor == streetCell.propertyStreet.streetColor)) {
                cellsSameColor.add(cell)
            }
        }

        return cellsSameColor
    }

    private fun ownsAllSameColor(cellsSameColor: List<StreetCell>, playerIndex: Int): Boolean {
        return cellsSameColor.all {
            it.propertyStreet.ownerIndex == playerIndex
        }
    }


    private fun canSellUpgrade(cellsSameColor: List<StreetCell>, playerIndex: Int, cell: StreetCell): Boolean {
        if (!ownsAllSameColor(cellsSameColor, playerIndex)) {
            return false
        }

        val levelUpdate = cell.propertyStreet.improvementLevel

        var maxUpgradeLevel = levelUpdate

        for (cellSameColor in cellsSameColor) {
            maxUpgradeLevel = max(maxUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
        }

        return (levelUpdate == maxUpgradeLevel) && (levelUpdate > 0)
    }

    private fun canBuyUpgrade(
        cellsSameColor: List<StreetCell>,
        playerIndex: Int,
        cell: StreetCell,
        balance: Int
    ): Boolean {
        if (!ownsAllSameColor(cellsSameColor, playerIndex)) {
            return false
        }

        val levelUpdate = cell.propertyStreet.improvementLevel

        var minUpgradeLevel = levelUpdate

        for (cellSameColor in cellsSameColor) {
            minUpgradeLevel = min(minUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
        }

        val upgradeCost = if (levelUpdate < 4) cell.propertyStreet.cost else cell.propertyStreet.cost * 2

        return (levelUpdate == minUpgradeLevel) && (levelUpdate < 5) && (balance >= upgradeCost)
    }

    private fun isSellUpgradeActionAvailable(gameState: GameState, gameAction: SellUpgradeAction): Boolean {
        val cell = gameState.cells[gameAction.cellId]

        when (cell) {
            is StreetCell -> {
                val cellsSameColor = getCellsSameColor(gameState.cells, streetCell = cell)

                return canSellUpgrade(cellsSameColor, gameState.playerTurn, cell)
            }

            else -> return false
        }
    }

    private fun isBuyUpgradeActionAvailable(gameState: GameState, gameAction: BuyUpgradeAction): Boolean {
        val cell = gameState.cells[gameAction.cellId]

        val player = gameState.players[gameState.playerTurn]

        when (cell) {
            is StreetCell -> {
                val cellsSameColor = getCellsSameColor(gameState.cells, streetCell = cell)

                return canBuyUpgrade(
                    cellsSameColor,
                    playerIndex = gameState.playerTurn,
                    cell = cell,
                    balance = player.balance
                )
            }

            else -> return false
        }
    }


    private fun isBuyPropertyActionAvailable(gameState: GameState, gameAction: BuyPropertyAction): Boolean {
        if (gameState.gameTurnPhase != GameTurnPhase.BUY_PROPERTY) {
            return false
        }

        val player = gameState.players[gameState.playerTurn]

        val cell = gameState.cells[player.position]

        if (cell != gameState.cells[gameAction.cellId]) {
            return false
        }

        when (cell) {
            is StreetCell -> {
                if (cell.propertyStreet.ownerIndex != null) {
                    return false
                }

                return player.balance >= cell.propertyStreet.cost
            }

            else -> return false
        }
    }

    private fun isEndGameActionAvailable(gameState: GameState, gameAction: EndTurnAction): Boolean {
        val gameTurnPhase = gameState.gameTurnPhase

        return (gameTurnPhase == GameTurnPhase.END_TURN) || (gameTurnPhase == GameTurnPhase.BUY_PROPERTY)
    }

    private fun isThrowDiceActionAvailable(gameState: GameState, gameAction: ThrowDiceAction): Boolean {
        val gameTurnPhase = gameState.gameTurnPhase
        val player = gameState.players[gameState.playerTurn]

        return (gameTurnPhase == GameTurnPhase.START_TURN) && (player.playerState != PlayerState.NOT_IN_GAME)
    }
}