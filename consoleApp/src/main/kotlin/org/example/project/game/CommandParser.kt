package org.example.project.game

import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.ThrowDiceAction

class CommandParser {
    fun parse(input: String, availableActions: List<GameAction>): CommandCLI {
        val inputSplit = input.split(" ")

        val command = inputSplit[0]
        val cellId = inputSplit.getOrNull(1)?.toIntOrNull()

        if (command == "select") {
            if (cellId != null) {
                return SelectCellCommand(cellId)
            }

            return InvalidCommand("Не введён cell_id через пробел (cell_id - число, номер клетки)")
        }

        val availableActionsMap = availableActions.toMap()

        val action = availableActionsMap[command]

        if (action == null) {
            return InvalidCommand("Команда не доступна или не существует")
        }

        return GameActionCommand(action = action)
    }


    private fun List<GameAction>.toMap(): Map<String, GameAction> {
        return this.associate {
            when(it) {
                is ThrowDiceAction -> "t" to it
                is BuyPropertyAction -> "b" to it
                is BuyUpgradeAction -> "bu" to it
                is SellUpgradeAction -> "su" to it
                is EndTurnAction -> "e" to it
            }
        }
    }
}