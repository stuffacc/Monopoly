package org.example.project.data.models.cell

import androidx.compose.ui.graphics.Color
import org.example.project.UI.screen.game.GameState
import org.example.project.data.models.player.Player
import kotlin.math.max
import kotlin.math.min


sealed class Property(
    open val name: String,
    open val cost: Int,
    open var owner: Player? = null
)

data class PropertyStreet(
    override val name: String,
    override val cost: Int,
    override var owner: Player? = null,
    val streetColor: Color,
    var improvementLevel: Int = 0
) : Property(name = name, cost = cost, owner = owner)


fun getCellsSameColor(gameState: GameState, streetCell: StreetCell): List<StreetCell> {
    val cellsSameColor = mutableListOf<StreetCell>()

    for (cell in gameState.cells) {
        if ((cell is StreetCell) && (cell.propertyStreet.streetColor == streetCell.propertyStreet.streetColor)) {
            cellsSameColor.add(cell)
        }
    }

    return cellsSameColor
}

fun ownsAllSameColor(cellsSameColor: List<StreetCell>, player: Player): Boolean {
    return cellsSameColor.all {
        it.propertyStreet.owner == player
    }
}


fun canSellUpgrade(cellsSameColor: List<StreetCell>, player: Player, cell: StreetCell): Boolean {
    if (!ownsAllSameColor(cellsSameColor, player)) {
        return false
    }

    val levelUpdate = cell.propertyStreet.improvementLevel

    var maxUpgradeLevel = levelUpdate

    for (cellSameColor in cellsSameColor) {
        maxUpgradeLevel = max(maxUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
    }

    return (levelUpdate == maxUpgradeLevel) && (levelUpdate > 0)
}

fun canBuyUpgrade(cellsSameColor: List<StreetCell>, player: Player, cell: StreetCell): Boolean {
    if (!ownsAllSameColor(cellsSameColor, player)) {
        return false
    }

    val levelUpdate = cell.propertyStreet.improvementLevel

    var minUpgradeLevel = levelUpdate

    for (cellSameColor in cellsSameColor) {
        minUpgradeLevel = min(minUpgradeLevel, cellSameColor.propertyStreet.improvementLevel)
    }

    val upgradeCost = if (levelUpdate < 4) cell.propertyStreet.cost else cell.propertyStreet.cost * 2

    return (levelUpdate == minUpgradeLevel) && (levelUpdate < 5) && (player.balance >= upgradeCost)
}