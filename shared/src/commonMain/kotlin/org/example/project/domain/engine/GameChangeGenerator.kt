package org.example.project.domain.engine

import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.cell.ChanceCell
import org.example.project.domain.models.cell.CommunityChestCell
import org.example.project.domain.models.cell.GoToJailCell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.cell.TaxCell
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.ChangeGamePhase
import org.example.project.domain.models.game.ChangeGameStateProgress
import org.example.project.domain.models.game.ChangePlayerState
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameChange
import org.example.project.domain.models.game.GameStateProgress
import org.example.project.domain.models.game.GameTurnPhase
import org.example.project.domain.models.game.MakeTransaction
import org.example.project.domain.models.game.NextTurnGame
import org.example.project.domain.models.game.PlayerMoved
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.SetPlayerDoubleCount
import org.example.project.domain.models.game.SetPropertyOwner
import org.example.project.domain.models.game.SetRecentDices
import org.example.project.domain.models.game.SetTurnsInJail
import org.example.project.domain.models.game.SetUpgradeLevel
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.PlayerState

object GameChangeGenerator {
    fun processAction(gameState: GameState, gameAction: GameAction): List<GameChange> {
        if (!GameActionValidator.isActionAvailable(gameState, gameAction)) {
            return listOf(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }
        // TODO: END, BUY if unavailable END TURN

        return when (gameAction) {
            is ThrowDiceAction -> processThrowDiceAction(gameState, gameAction)
            is BuyPropertyAction -> processBuyPropertyAction(gameState, gameAction)
            is BuyUpgradeAction -> processBuyUpgradeAction(gameState, gameAction)
            is SellUpgradeAction -> processSellUpgradeAction(gameState, gameAction)
            is EndTurnAction -> processEndTurnAction(gameState, gameAction)
        }
    }

    private fun processSellUpgradeAction(gameState: GameState, gameAction: SellUpgradeAction): List<GameChange> {
        val cell = gameState.cells[gameAction.cellId]

        when (cell) {
            is StreetCell -> {
                val levelUpdate = cell.propertyStreet.improvementLevel

                val sellCost = if (levelUpdate < 5) cell.propertyStreet.cost / 2 else cell.propertyStreet.cost

                return listOf(
                    MakeTransaction(
                        fromPlayerIndex = null,
                        toPlayerIndex = gameState.playerTurn,
                        amount = sellCost
                    ),

                    SetUpgradeLevel(
                        cellIndex = gameAction.cellId,
                        upgradeLevel = levelUpdate - 1
                    )
                )
            }

            else -> return emptyList()
        }
    }

    private fun processBuyUpgradeAction(gameState: GameState, gameAction: BuyUpgradeAction): List<GameChange> {
        val cell = gameState.cells[gameAction.cellId]
        val player = gameState.players[gameState.playerTurn]

        return when (cell) {
            is StreetCell -> {
                val levelUpdate = cell.propertyStreet.improvementLevel

                val upgradeCost = if (levelUpdate < 4) cell.propertyStreet.cost else cell.propertyStreet.cost * 2

                listOf(
                    MakeTransaction(
                        fromPlayerIndex = gameState.playerTurn,
                        toPlayerIndex = null,
                        amount = upgradeCost
                    ),

                    SetUpgradeLevel(
                        cellIndex = gameAction.cellId,
                        upgradeLevel = levelUpdate + 1
                    )
                )
            }

            else -> emptyList()
        }
    }

    private fun processEndTurnAction(gameState: GameState, gameAction: EndTurnAction): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        val player = gameState.players[gameState.playerTurn]

        val countNotInGame = gameState.players.count {
            it.playerState == PlayerState.NOT_IN_GAME
        }


        if (countNotInGame == gameState.players.size - 1) {
            gameChanges.add(
                ChangeGameStateProgress(
                    nextStateProgress = GameStateProgress.FINISHED
                )
            )
        } else if (player.doubleCount > 0 && player.playerState != PlayerState.NOT_IN_GAME) {
            gameChanges.add(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.START_TURN
                )
            )
        } else {
            var nextIndexPlayer = gameState.playerTurn

            do {
                nextIndexPlayer = (nextIndexPlayer + 1) % gameState.players.size
                val nextPlayer = gameState.players[nextIndexPlayer]
            } while (nextPlayer.playerState == PlayerState.NOT_IN_GAME)

            gameChanges.add(
                NextTurnGame(
                    playerIndexBefore = gameState.playerTurn,
                    playerIndexAfter = nextIndexPlayer
                )
            )

            gameChanges.add(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.START_TURN
                )
            )
        }

        return gameChanges
    }


    private fun processBuyPropertyAction(gameState: GameState, gameAction: BuyPropertyAction): List<GameChange> {
        val player = gameState.players[gameState.playerTurn]

        val cell = gameState.cells[player.position]

        when (cell) {
            is StreetCell -> {
                return listOf(
                    MakeTransaction(
                        fromPlayerIndex = gameState.playerTurn,
                        toPlayerIndex = null,
                        amount = cell.propertyStreet.cost
                    ),
                    SetPropertyOwner(
                        playerIndex = gameState.playerTurn,
                        propertyIndex = player.position
                    ),
                )
            }

            else -> return listOf(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }
    }


    private fun processThrowDiceAction(gameState: GameState, gameAction: ThrowDiceAction): List<GameChange> {
        val player = gameState.players[gameState.playerTurn]

        val gameChanges = mutableListOf<GameChange>()

        gameChanges.add(
            SetRecentDices(Pair(gameAction.dice1, gameAction.dice2))
        )

        if (player.playerState == PlayerState.IN_JAIL) {
            gameChanges.addAll(tryLeaveJail(gameState, gameAction))
            return gameChanges
        }

        gameChanges.addAll(makeTurn(gameState, gameAction))

        return gameChanges
    }

    private fun makeTurn(gameState: GameState, gameAction: ThrowDiceAction): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val player = gameState.players[gameState.playerTurn]

        if (gameAction.dice1 == gameAction.dice2) {
            if (player.doubleCount + 1 == 3) {
                gameChanges.addAll(goToJail(gameState = gameState))

                return gameChanges

            } else {
                gameChanges.add(
                    SetPlayerDoubleCount(
                        playerIndex = gameState.playerTurn,
                        doubleCount = player.doubleCount + 1
                    )
                )
            }
        } else {
            gameChanges.add(
                SetPlayerDoubleCount(
                    playerIndex = gameState.playerTurn,
                    doubleCount = 0
                )
            )
        }

        gameChanges.addAll(movePlayer(gameState, gameAction))

        return gameChanges
    }

    private fun movePlayer(gameState: GameState, gameAction: ThrowDiceAction): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val player = gameState.players[gameState.playerTurn]

        val isGoCellWent = ((player.position + (gameAction.dice1 + gameAction.dice2)) >= gameState.cells.size)

        if (isGoCellWent) {
            gameChanges.add(
                MakeTransaction(
                    fromPlayerIndex = null,
                    toPlayerIndex = gameState.playerTurn,
                    amount = 200
                )
            )
        }


        gameChanges.add(
            PlayerMoved(
                playerIndex = gameState.playerTurn,
                from = player.position,
                value = gameAction.dice1 + gameAction.dice2,
                to = (player.position + (gameAction.dice1 + gameAction.dice2)) % gameState.cells.size
            )
        )

        val cell = gameState.cells[(player.position + (gameAction.dice1 + gameAction.dice2)) % gameState.cells.size]

        when (cell) {
            is StreetCell -> {
                gameChanges.addAll(playerArrivedToStreetCell(gameState, cell))
            }

            is TaxCell -> {
                gameChanges.addAll(playerArrivedToTaxCell(gameState = gameState, taxCell = cell))
            }

            is ChanceCell, is CommunityChestCell, is GoToJailCell -> {
                gameChanges.addAll(goToJail(gameState))
            }

            else -> {
                gameChanges.add(
                    ChangeGamePhase(
                        previousPhase = gameState.gameTurnPhase,
                        nextPhase = GameTurnPhase.END_TURN
                    )
                )
            }
        }

        return gameChanges
    }

    private fun playerArrivedToStreetCell(gameState: GameState, cell: StreetCell): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val propertyOwnerIndex = cell.propertyStreet.ownerIndex
        val playerIndex = gameState.playerTurn

        if (propertyOwnerIndex == null) {
            gameChanges.add(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.BUY_PROPERTY
                )
            )
        } else if (propertyOwnerIndex != playerIndex) {
            gameChanges.add(
                MakeTransaction(
                    fromPlayerIndex = gameState.playerTurn,
                    toPlayerIndex = propertyOwnerIndex,
                    amount = cell.propertyStreet.cost
                )
            )

            gameChanges.add(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )

        } else {
            gameChanges.add(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }

        return gameChanges
    }

    private fun tryLeaveJail(gameState: GameState, gameAction: ThrowDiceAction): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        val player = gameState.players[gameState.playerTurn]

        if (gameAction.dice1 == gameAction.dice2) {
            gameChanges.addAll(leaveTheJail(gameState))

            gameChanges.addAll(movePlayer(gameState, gameAction))

            return gameChanges
        }

        if (player.turnsInJail == 3) {
            gameChanges.addAll(leaveTheJail(gameState))

            gameChanges.add(
                MakeTransaction(
                    fromPlayerIndex = gameState.playerTurn,
                    toPlayerIndex = null,
                    amount = 50
                )
            )

            gameChanges.addAll(movePlayer(gameState, gameAction))

            return gameChanges
        }

        gameChanges.add(
            SetTurnsInJail(
                playerIndex = gameState.playerTurn,
                turnsInJail = player.turnsInJail + 1
            )
        )

        gameChanges.add(
            ChangeGamePhase(
                previousPhase = gameState.gameTurnPhase,
                nextPhase = GameTurnPhase.END_TURN
            )
        )

        return gameChanges

    }

    private fun leaveTheJail(gameState: GameState): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        gameChanges.add(
            SetTurnsInJail(
                playerIndex = gameState.playerTurn,
                turnsInJail = 0
            )
        )

        gameChanges.add(
            SetPlayerDoubleCount(
                playerIndex = gameState.playerTurn,
                doubleCount = 0
            )
        )

        gameChanges.add(
            ChangePlayerState(
                playerIndex = gameState.playerTurn,
                newState = PlayerState.IN_GAME
            )
        )

        return gameChanges
    }


    private fun goToJail(gameState: GameState): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        val player = gameState.players[gameState.playerTurn]

        gameChanges.add(
            SetPlayerDoubleCount(
                playerIndex = gameState.playerTurn,
                doubleCount = 0
            )
        )

        gameChanges.add(
            PlayerMoved(
                playerIndex = gameState.playerTurn,
                from = player.position,
                value = 0,
                to = 10
            )
        )

        gameChanges.add(
            ChangePlayerState(
                playerIndex = gameState.playerTurn,
                newState = PlayerState.IN_JAIL
            )
        )

        gameChanges.add(
            ChangeGamePhase(
                previousPhase = gameState.gameTurnPhase,
                nextPhase = GameTurnPhase.END_TURN
            )
        )

        return gameChanges
    }

    private fun playerArrivedToTaxCell(gameState: GameState, taxCell: TaxCell): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        gameChanges.add(
            MakeTransaction(
                fromPlayerIndex = gameState.playerTurn,
                toPlayerIndex = null,
                amount = taxCell.taxValue
            )
        )

        gameChanges.add(
            ChangeGamePhase(
                previousPhase = gameState.gameTurnPhase,
                nextPhase = GameTurnPhase.END_TURN
            )
        )

        return gameChanges
    }
}