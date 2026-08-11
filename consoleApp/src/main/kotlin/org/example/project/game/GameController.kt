package org.example.project.game

import org.example.project.domain.models.game.*
import org.example.project.domain.usecase.GetAvailableActionUsecase
import org.example.project.domain.usecase.LoadGameUsecase
import org.example.project.domain.usecase.SendActionUsecase

class GameController(
    private val loadGameUsecase: LoadGameUsecase,
    private val getAvailableActionUsecase: GetAvailableActionUsecase,
    private val sendActionUsecase: SendActionUsecase
) {
    private var gameState: GameState = GameState()
    private var availableActions: List<GameAction> = emptyList()

    private val render = Render()
    private val parser = CommandParser()

    fun run(gameId: String) {
        gameState = loadGame(gameId)

        while (!gameState.isFinished) {
            render.printGameState(gameState)

            availableActions = getAvailableAction(gameState.players[gameState.playerTurn].position)
            render.printAvailableActions(availableActions)

            while (true) {
                val input = readln()
                val commandCLI = parser.parse(input, availableActions)

                when (commandCLI) {
                    is GameActionCommand -> {
                        gameState = sendActionUsecase.execute(gameState, gameAction = commandCLI.action)
                        break
                    }
                    is InvalidCommand -> {
                        println(commandCLI.message)
                    }
                    is SelectCellCommand -> {
                        render.printPlayerInfo(gameState)
                        render.printDetailedCell(gameState, cellId = commandCLI.cellId)
                        availableActions = getAvailableAction(cellId = commandCLI.cellId)
                    }
                }

                render.printAvailableActions(availableActions)
            }
        }
    }

    private fun loadGame(gameId: String): GameState {
        return loadGameUsecase.execute(gameId = gameId)
    }

    private fun getAvailableAction(cellId: Int): List<GameAction> {
        return getAvailableActionUsecase.execute(gameState, cellId)
    }
}