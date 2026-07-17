package org.example.project.data.models.cell

import androidx.compose.ui.graphics.Color
import org.example.project.UI.screen.game.GameState
import org.example.project.data.models.player.Player
import kotlin.math.max
import kotlin.math.min


sealed class Property(
    open val name: String,
    open val cost: Int,
    open val ownerIndex: Int? = null
)

data class PropertyStreet(
    override val name: String,
    override val cost: Int,
    override val ownerIndex: Int? = null,
    val streetColor: Color,
    val improvementLevel: Int = 0
) : Property(name = name, cost = cost, ownerIndex = ownerIndex)


fun getCellsSameColor(gameState: GameState, streetCell: StreetCell): List<StreetCell> {
    val cellsSameColor = mutableListOf<StreetCell>()

    for (cell in gameState.cells) {
        if ((cell is StreetCell) && (cell.propertyStreet.streetColor == streetCell.propertyStreet.streetColor)) {
            cellsSameColor.add(cell)
        }
    }

    return cellsSameColor
}

fun ownsAllSameColor(cellsSameColor: List<StreetCell>, gameState: GameState): Boolean {
    val playerIndex = gameState.playerTurn

    return cellsSameColor.all {
        it.propertyStreet.ownerIndex == playerIndex
    }
}


fun canSellUpgrade(cellsSameColor: List<StreetCell>, gameState: GameState, cell: StreetCell): Boolean {
    if (!ownsAllSameColor(cellsSameColor, gameState)) {
        return false
    }

    val levelUpdate = cell.propertyStreet.improvementLevel

    var maxUpgradeLevel = levelUpdate

    for (cellSameColor in cellsSameColor) {
        maxUpgradeLevel = max(maxUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
    }

    return (levelUpdate == maxUpgradeLevel) && (levelUpdate > 0)
}

fun canBuyUpgrade(cellsSameColor: List<StreetCell>, gameState: GameState, cell: StreetCell): Boolean {
    if (!ownsAllSameColor(cellsSameColor, gameState)) {
        return false
    }

    val levelUpdate = cell.propertyStreet.improvementLevel

    var minUpgradeLevel = levelUpdate

    for (cellSameColor in cellsSameColor) {
        minUpgradeLevel = min(minUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
    }

    val upgradeCost = if (levelUpdate < 4) cell.propertyStreet.cost else cell.propertyStreet.cost * 2

    val player = gameState.players[gameState.playerTurn]

    return (levelUpdate == minUpgradeLevel) && (levelUpdate < 5) && (player.balance >= upgradeCost)
}