package org.example.project.domain.engine

import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.game.ChangeGamePhase
import org.example.project.domain.models.game.ChangeGameStateProgress
import org.example.project.domain.models.game.ChangePlayerState
import org.example.project.domain.models.game.GameChange
import org.example.project.domain.models.game.GameTurnPhase
import org.example.project.domain.models.game.MakeTransaction
import org.example.project.domain.models.game.NextTurnGame
import org.example.project.domain.models.game.PlayerMoved
import org.example.project.domain.models.game.SetPlayerDoubleCount
import org.example.project.domain.models.game.SetPropertyOwner
import org.example.project.domain.models.game.SetRecentDices
import org.example.project.domain.models.game.SetTurnsInJail
import org.example.project.domain.models.game.SetUpgradeLevel
import org.example.project.domain.models.player.Player
import org.example.project.domain.models.player.PlayerState
import kotlin.math.min

object GameChangeApplier {
    fun applyGameChanges(gameState: GameState, gameChanges: List<GameChange>): GameState {
        return gameChanges.fold(initial = gameState) { lastGameState, gameChange ->
            when (gameChange) {
                is MakeTransaction -> makeTransactionGameChange(lastGameState, gameChange)
                is ChangePlayerState -> changePlayerStateGameChange(lastGameState, gameChange)
                is PlayerMoved -> playerMovedGameChange(lastGameState, gameChange)
                is SetPropertyOwner -> setPropertyOwnerGameChange(lastGameState, gameChange)
                is ChangeGamePhase -> changeGamePhaseGameChange(lastGameState, gameChange)
                is SetPlayerDoubleCount -> setPlayerDoubleCountGameChange(lastGameState, gameChange)
                is NextTurnGame -> nextTurnGameGameChange(lastGameState, gameChange)
                is ChangeGameStateProgress -> changeGameStateProgressGameChange(lastGameState, gameChange)
                is SetTurnsInJail -> setTurnsInJailGameChange(lastGameState, gameChange)
                is SetUpgradeLevel -> setUpgradeLevelGameChange(lastGameState, gameChange)
                is SetRecentDices -> setRecentDicesGameChange(lastGameState, gameChange)
            }
        }
    }

    fun setRecentDicesGameChange(gameState: GameState, gameChange: SetRecentDices): GameState {
        return gameState.copy(
            lastDices = gameChange.dices
        )
    }

    private fun makeTransactionGameChange(gameState: GameState, gameChange: MakeTransaction): GameState {
        // TODO: Хотелось сделать чтобы gameChange были без логики,
        //  но тут скорее всего никак по-другому не сделать. Другие варианты менее красивые
        var playerFrom = if (gameChange.fromPlayerIndex != null) gameState.players[gameChange.fromPlayerIndex] else null
        var playerTo = if (gameChange.toPlayerIndex != null) gameState.players[gameChange.toPlayerIndex] else null

        var canPayFromPlayer = gameChange.amount

        if (playerFrom != null) {
            canPayFromPlayer = min(playerFrom.balance, gameChange.amount)

            playerFrom = playerFrom.copy(
                balance = playerFrom.balance - canPayFromPlayer
            )
        }

        if (playerTo != null) {
            playerTo = playerTo.copy(
                balance = playerTo.balance + canPayFromPlayer
            )
        }

        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if ((playerFrom != null) && (index == gameChange.fromPlayerIndex)) playerFrom
            else if ((playerTo != null) && (index == gameChange.toPlayerIndex)) playerTo
            else player
        }


        val newGameState = gameState.copy(
            players = newPlayers
        )

        if (canPayFromPlayer < gameChange.amount) {
            return eliminatePlayer(newGameState, gameChange.fromPlayerIndex!!)
        }

        return newGameState
    }

    private fun eliminatePlayer(gameState: GameState, playerIndex: Int): GameState {
        val gameChanges = mutableListOf<GameChange>()

        gameChanges.add(
            ChangePlayerState(
                playerIndex = playerIndex,
                newState = PlayerState.NOT_IN_GAME
            )
        )

        for (i in gameState.cells.indices) {
            val cellToRemoveOwner = gameState.cells[i]
            if (cellToRemoveOwner is StreetCell) {
                if (cellToRemoveOwner.propertyStreet.ownerIndex == playerIndex) {
                    gameChanges.add(
                        SetPropertyOwner(
                            playerIndex = null,
                            propertyIndex = i
                        )
                    )

                    gameChanges.add(
                        SetUpgradeLevel(
                            cellIndex = i,
                            upgradeLevel = 0
                        )
                    )
                }
            }
        }

        gameChanges.add(
            ChangeGamePhase(
                previousPhase = gameState.gameTurnPhase,
                nextPhase = GameTurnPhase.END_TURN
            )
        )

        return applyGameChanges(gameState, gameChanges)
    }


    private fun setUpgradeLevelGameChange(gameState: GameState, gameChange: SetUpgradeLevel): GameState {
        val newCells: List<Cell> = gameState.cells.mapIndexed { index, cell ->
            (if ((index == gameChange.cellIndex) && (cell is StreetCell))
                StreetCell(
                    propertyStreet =
                        cell.propertyStreet.copy(
                            improvementLevel = gameChange.upgradeLevel
                        )
                )
            else cell)
        }

        return gameState.copy(cells = newCells)
    }

    private fun setTurnsInJailGameChange(gameState: GameState, gameChange: SetTurnsInJail): GameState {
        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if (index == gameChange.playerIndex)
                player.copy(
                    turnsInJail = gameChange.turnsInJail
                )
            else player
        }

        return gameState.copy(
            players = newPlayers
        )
    }

    private fun changeGameStateProgressGameChange(gameState: GameState, gameChange: ChangeGameStateProgress): GameState {
        return gameState.copy(
            gameStateProgress = gameChange.nextStateProgress
        )
    }

    private fun nextTurnGameGameChange(gameState: GameState, gameChange: NextTurnGame): GameState {
        return gameState.copy(
            playerTurn = gameChange.playerIndexAfter
        )
    }

    private fun setPlayerDoubleCountGameChange(gameState: GameState, gameChange: SetPlayerDoubleCount): GameState {
        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if (index == gameChange.playerIndex)
                player.copy(
                    doubleCount = gameChange.doubleCount
                )
            else player
        }

        return gameState.copy(
            players = newPlayers
        )
    }

    private fun changeGamePhaseGameChange(gameState: GameState, gameChange: ChangeGamePhase): GameState {
        return gameState.copy(
            gameTurnPhase = gameChange.nextPhase
        )
    }


    private fun changePlayerStateGameChange(gameState: GameState, gameChange: ChangePlayerState): GameState {
        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if (index == gameChange.playerIndex)
                player.copy(
                    playerState = gameChange.newState
                )
            else player
        }

        return gameState.copy(
            players = newPlayers
        )
    }

    private fun playerMovedGameChange(gameState: GameState, gameChange: PlayerMoved): GameState {
        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if (index == gameChange.playerIndex)
                player.copy(
                    position = gameChange.to
                )
            else player
        }

        return gameState.copy(
            players = newPlayers
        )
    }

    private fun setPropertyOwnerGameChange(gameState: GameState, gameChange: SetPropertyOwner): GameState {
        val newCells: List<Cell> = gameState.cells.mapIndexed { index, cell ->
            (if ((index == gameChange.propertyIndex) && (cell is StreetCell))
                StreetCell(
                    propertyStreet =
                        cell.propertyStreet.copy(
                            ownerIndex = gameChange.playerIndex
                        )
                )
            else cell)
        }

        return gameState.copy(cells = newCells)
    }

}