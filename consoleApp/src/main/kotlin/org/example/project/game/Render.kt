package org.example.project.game

import org.example.project.domain.models.Color
import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.cell.TaxCell
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.Player

class Render {
    fun printGameState(gameState: GameState) {
        println("------------------------------------------------------------------------------")
        val currentPlayer = gameState.players[gameState.playerTurn]

        printColored(color = currentPlayer.color, text = "Ходит игрок: ${currentPlayer.name}")


        printField(gameState)

        printPlayerInfo(gameState)

        printDetailedCell(gameState, cellId = currentPlayer.position)

        println("Кубики: (${gameState.lastDices.first}, ${gameState.lastDices.second})\n")
    }

    fun printPlayerInfo(gameState: GameState) {
        val currentPlayer = gameState.players[gameState.playerTurn]

        print("\nИмя: ")
        printColored(color = currentPlayer.color, text = currentPlayer.name)

        println("\nБаланс: ${currentPlayer.balance}")
        println("Позиция: ${currentPlayer.position}")
        println()
    }

    private fun printField(gameState: GameState) {
        val cells = gameState.cells
        val groupPlayersByPosition = gameState.players.groupBy {
            it.position
        }
        print("\n\n")

        for (i in cells.indices) {
            val cell = cells[i]
            print("|$i ")
            printCell(cell, groupPlayersByPosition[i])
            print("| ")
        }
        println()
    }

    private fun printCell(cell: Cell, players: List<Player>?) {
        when (cell) {
            is StreetCell -> {
                printColored(color = cell.propertyStreet.streetColor, text = cell.name)
            }

            else -> {
                print(cell.name)
            }
        }

        printPlayers(players)
    }

    private fun printPlayers(players: List<Player>?) {
        if (players.isNullOrEmpty()) {
            return
        }

        print(" (")
        for (i in players.indices) {
            val player = players[i]
            printColored(color = player.color, text = player.name)

            if (i != players.size - 1) {
                print(", ")
            }
        }
        print(")")
    }

    private fun printColored(color: Color, text: String) {
        print("${color.toANSIBackgroundColorWithReadableTextColor()}$text\u001B[0m")
    }

    private fun Color.toANSIBackgroundColorWithReadableTextColor(): String {
        return when (this) {
            Color.BROWN -> "\u001B[48;5;94m\u001B[38;5;231m"   // чёрный текст
            Color.YELLOW -> "\u001B[48;5;226m\u001B[38;5;16m"  // чёрный текст
            Color.BLUE -> "\u001B[48;5;39m\u001B[38;5;16m"  // чёрный текст
            Color.DARK_BLUE -> "\u001B[48;5;17m\u001B[38;5;231m" // белый текст
            Color.VIOLET -> "\u001B[48;5;129m\u001B[38;5;231m" // белый текст
            Color.ORANGE -> "\u001B[48;5;214m\u001B[38;5;16m"  // чёрный текст
            Color.RED -> "\u001B[48;5;196m\u001B[38;5;231m" // белый текст
            Color.GREEN -> "\u001B[48;5;46m\u001B[38;5;16m"   // чёрный текст
        }
    }


    fun printDetailedCell(gameState: GameState, cellId: Int) {
        print("\nКлетка: $cellId")
        print("\nНазвание: ")

        val cell = gameState.cells[cellId]

        when (cell) {
            is StreetCell -> {
                printColored(color = cell.propertyStreet.streetColor, text = cell.name)

                print("\nВладелец: ")

                if (cell.propertyStreet.ownerIndex == null) {
                    print("нет")
                } else {
                    val owner = gameState.players[cell.propertyStreet.ownerIndex!!]
                    printColored(color = owner.color, text = owner.name)
                }

                println("\nУровень улучшения: ${cell.propertyStreet.improvementLevel}")
                println("Стоимость: ${cell.propertyStreet.cost}")
            }

            is TaxCell -> {
                println(cell.name)
                println("Налог: ${cell.taxValue}")
            }

            else -> {
                println(cell.name)
            }
        }

        println()
    }

    fun printAvailableActions(availableActions: List<GameAction>) {
        println("\nДоступные действия: ")
        println("select {cell_id} - Информация о клетке с индексом {cell_id}, доступные действия с ней")
        for (i in availableActions.indices) {
            when (availableActions[i]) {
                is ThrowDiceAction -> println("t - Бросить кубик")
                is BuyPropertyAction -> println("b - Купить собственность")
                is BuyUpgradeAction -> println("bu - Купить улучшение")
                is SellUpgradeAction -> println("su - Продать улучшение")
                is EndTurnAction -> println("e - Закончить ход")
            }
        }
    }
}