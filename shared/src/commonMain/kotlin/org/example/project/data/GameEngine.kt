package org.example.project.data

import org.example.project.data.models.cell.ChanceCell
import org.example.project.data.models.cell.CommunityChestCell
import org.example.project.data.models.cell.GoToJailCell
import org.example.project.data.models.cell.StreetCell
import org.example.project.data.models.cell.TaxCell
import org.example.project.data.models.game.BuyPropertyAction
import org.example.project.data.models.game.BuyUpgradeAction
import org.example.project.data.models.game.ChangeGamePhase
import org.example.project.data.models.game.ChangeGameStateProgress
import org.example.project.data.models.game.ChangePlayerState
import org.example.project.data.models.game.EndTurnAction
import org.example.project.data.models.game.GameAction
import org.example.project.data.models.game.GameChange
import org.example.project.UI.screen.game.GameState
import org.example.project.data.models.cell.Cell
import org.example.project.data.models.cell.canBuyUpgrade
import org.example.project.data.models.cell.canSellUpgrade
import org.example.project.data.models.cell.getCellsSameColor
import org.example.project.data.models.game.GameStateProgress
import org.example.project.data.models.game.GameTurnPhase
import org.example.project.data.models.game.MakeTransaction
import org.example.project.data.models.game.NextTurnGame
import org.example.project.data.models.game.PlayerMoved
import org.example.project.data.models.game.SellUpgradeAction
import org.example.project.data.models.game.SetPlayerDoubleCount
import org.example.project.data.models.game.SetPropertyOwner
import org.example.project.data.models.game.SetTurnsInJail
import org.example.project.data.models.game.SetUpgradeLevel
import org.example.project.data.models.game.ThrowDiceAction
import org.example.project.data.models.player.Player
import org.example.project.data.models.player.PlayerState
import kotlin.math.max
import kotlin.math.min

object GameEngine {
    fun handle(gameState: GameState, gameAction: GameAction): GameState {
        return when (gameAction) {
            is ThrowDiceAction -> processThrowDiceAction(gameState, gameAction)
            is BuyPropertyAction -> processBuyPropertyAction(gameState, gameAction)
            is BuyUpgradeAction -> processBuyUpgradeAction(gameState, gameAction)
            is SellUpgradeAction -> processSellUpgradeAction(gameState, gameAction)
            is EndTurnAction -> processEndTurnAction(gameState, gameAction)
        }
    }

    private fun processSellUpgradeAction(gameState: GameState, gameAction: SellUpgradeAction): GameState {
        val gameChanges = isSellUpgradeActionAvailable(gameState, gameAction)

        return applyGameChanges(gameState, gameChanges)
    }

    private fun isSellUpgradeActionAvailable(gameState: GameState, gameAction: SellUpgradeAction): List<GameChange> {
        val player = gameState.players[gameState.playerTurn]

        val cell = gameState.cells[gameAction.cellId]

        return when (cell) {
            is StreetCell -> {
                val cellsSameColor = getCellsSameColor(gameState, streetCell = cell)

                if (!canSellUpgrade(cellsSameColor, gameState, cell)) {
                    return emptyList()
                }

                val levelUpdate = cell.propertyStreet.improvementLevel

                val sellCost = if (levelUpdate < 5) cell.propertyStreet.cost / 2 else cell.propertyStreet.cost

                listOf(
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

            else -> emptyList()
        }
    }

    private fun processBuyUpgradeAction(gameState: GameState, gameAction: BuyUpgradeAction): GameState {
        val gameChanges = isBuyUpgradeActionAvailable(gameState, gameAction)

        return applyGameChanges(gameState, gameChanges)
    }

    private fun isBuyUpgradeActionAvailable(gameState: GameState, gameAction: BuyUpgradeAction): List<GameChange> {
        val player = gameState.players[gameState.playerTurn]

        val cell = gameState.cells[gameAction.cellId]

        return when (cell) {
            is StreetCell -> {
                val cellsSameColor = getCellsSameColor(gameState, streetCell = cell)

                if (!canBuyUpgrade(cellsSameColor, gameState, cell)) {
                    return emptyList()
                }

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

    private fun processEndTurnAction(gameState: GameState, gameAction: EndTurnAction): GameState {
        val gameChanges = isEndTurnActionAvailable(gameState, gameAction)

        return applyGameChanges(gameState, gameChanges)
    }

    private fun isEndTurnActionAvailable(gameState: GameState, gameAction: EndTurnAction): List<GameChange> {
        if (gameState.gameTurnPhase == GameTurnPhase.START_TURN) {
            return emptyList()
        }

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


    private fun processBuyPropertyAction(gameState: GameState, gameAction: BuyPropertyAction): GameState {
        val gameChanges = isBuyPropertyActionAvailable(gameState, gameAction)

        return applyGameChanges(gameState, gameChanges)
    }

    private fun isBuyPropertyActionAvailable(gameState: GameState, gameAction: BuyPropertyAction): List<GameChange> {
        if (gameState.gameTurnPhase != GameTurnPhase.BUY_PROPERTY) {
            return emptyList()
        }

        val player = gameState.players[gameState.playerTurn]

        val cell = gameState.cells[player.position]

        if (cell != gameState.cells[gameAction.cellId]) {
            return listOf(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }

        return when (cell) {
            is StreetCell -> {
                if (cell.propertyStreet.ownerIndex != null) {
                    return listOf(
                        ChangeGamePhase(
                            previousPhase = gameState.gameTurnPhase,
                            nextPhase = GameTurnPhase.END_TURN
                        )
                    )
                }

                if (player.balance >= cell.propertyStreet.cost) {
                    listOf(
                        MakeTransaction(
                            fromPlayerIndex = gameState.playerTurn,
                            toPlayerIndex = null,
                            amount = cell.propertyStreet.cost
                        ),
                        SetPropertyOwner(
                            playerIndex = gameState.playerTurn,
                            propertyIndex = player.position
                        ),
                        ChangeGamePhase(
                            previousPhase = gameState.gameTurnPhase,
                            nextPhase = GameTurnPhase.END_TURN
                        )
                    )
                } else {
                    listOf(
                        ChangeGamePhase(
                            previousPhase = gameState.gameTurnPhase,
                            nextPhase = GameTurnPhase.END_TURN
                        )
                    )
                }
            }

            else -> listOf(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }
    }

    private fun applyGameChanges(gameState: GameState, gameChanges: List<GameChange>): GameState {
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
            }
        }
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

        println()
        println(playerTo)
        println(playerFrom)
        println()

        val newPlayers: List<Player> = gameState.players.mapIndexed { index, player ->
            if ((playerFrom != null) && (index == gameChange.fromPlayerIndex)) playerFrom
            else if ((playerTo != null) && (index == gameChange.toPlayerIndex)) playerTo
            else player
        }
        println(newPlayers)
        println()


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

    private fun changeGameStateProgressGameChange(
        gameState: GameState,
        gameChange: ChangeGameStateProgress
    ): GameState {
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

    private fun processThrowDiceAction(gameState: GameState, gameAction: ThrowDiceAction): GameState {
        val dices = throwDices()

        return when (gameState.gameTurnPhase) {
            GameTurnPhase.START_TURN -> processThrowDiceActionStartTurnPhase(gameState, gameAction, dices)
            GameTurnPhase.BUY_PROPERTY -> gameState
            GameTurnPhase.END_TURN -> gameState
        }
    }

    private fun processThrowDiceActionStartTurnPhase(
        gameState: GameState,
        gameAction: ThrowDiceAction,
        dices: Pair<Int, Int>
    ): GameState {
        val gameChanges = isThrowDiceActionAvailable(gameState, gameAction, dices)

        return applyGameChanges(gameState, gameChanges).copy(
            lastDices = dices
        )
    }

    private fun isThrowDiceActionAvailable(
        gameState: GameState,
        gameAction: ThrowDiceAction,
        dices: Pair<Int, Int>
    ): List<GameChange> {
        val player = gameState.players[gameState.playerTurn]

        if (player.playerState == PlayerState.IN_JAIL) {
            return tryLeaveJail(gameState, dices)
        }

        if (player.playerState == PlayerState.NOT_IN_GAME) {
            return listOf(
                ChangeGamePhase(
                    previousPhase = gameState.gameTurnPhase,
                    nextPhase = GameTurnPhase.END_TURN
                )
            )
        }

        return makeTurn(gameState, dices)
    }

    private fun makeTurn(gameState: GameState, dices: Pair<Int, Int>): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val player = gameState.players[gameState.playerTurn]

        if (dices.first == dices.second) {
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

        gameChanges.addAll(movePlayer(gameState, dices))

        return gameChanges
    }

    private fun movePlayer(gameState: GameState, dices: Pair<Int, Int>): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val player = gameState.players[gameState.playerTurn]

        val isGoCellWent = ((player.position + (dices.first + dices.second)) >= gameState.cells.size)

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
                value = dices.first + dices.second,
                to = (player.position + (dices.first + dices.second)) % gameState.cells.size
            )
        )

        val cell = gameState.cells[(player.position + (dices.first + dices.second)) % gameState.cells.size]

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

    private fun findPlayerIndexById(players: List<Player>, playerId: String): Int? {
        for (i in players.indices) {
            val player = players[i]

            if (player.id == playerId) {
                return i
            }
        }

        return null
    }

    private fun playerArrivedToStreetCell(gameState: GameState, cell: StreetCell): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()
        val propertyOwnerIndex = cell.propertyStreet.ownerIndex
        val playerIndex = gameState.playerTurn

        if (propertyOwnerIndex == null) {
            println("propertyOwner == null")
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

    private fun tryLeaveJail(gameState: GameState, dices: Pair<Int, Int>): List<GameChange> {
        val gameChanges = mutableListOf<GameChange>()

        val player = gameState.players[gameState.playerTurn]

        if (dices.first == dices.second) {
            gameChanges.addAll(leaveTheJail(gameState))

            gameChanges.addAll(movePlayer(gameState, dices))

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

            gameChanges.addAll(movePlayer(gameState, dices))

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

    private fun throwDices(): Pair<Int, Int> {
        val dice1 = (1..6).random()
        val dice2 = (1..6).random()

        return Pair(dice1, dice2)
    }


    fun getAvailableActions(gameState: GameState, cellId: Int): List<GameAction> {
        val availableActionList = mutableListOf<GameAction>()

        if (isBuyUpgradeActionAvailable(gameState, BuyUpgradeAction(cellId)).isNotEmpty()) {
            availableActionList.add(BuyUpgradeAction(cellId))
        }

        if (isSellUpgradeActionAvailable(gameState, SellUpgradeAction(cellId)).isNotEmpty()) {
            availableActionList.add(SellUpgradeAction(cellId))
        }

        when (gameState.gameTurnPhase) {
            GameTurnPhase.START_TURN -> {
                availableActionList.add(ThrowDiceAction())
            }

            GameTurnPhase.BUY_PROPERTY -> {
                if (isBuyPropertyActionAvailable(gameState, BuyPropertyAction(cellId)).size > 1) {
                    availableActionList.add(BuyPropertyAction(cellId))
                }

                availableActionList.add(EndTurnAction())
            }

            GameTurnPhase.END_TURN -> {
                availableActionList.add(EndTurnAction())
            }
        }

        return availableActionList
    }
}