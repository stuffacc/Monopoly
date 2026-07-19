package org.example.project

import androidx.compose.ui.graphics.Color
import org.example.project.domain.models.game.GameState
import org.example.project.data.createField
import org.example.project.domain.engine.GameActionValidator
import org.example.project.domain.engine.GameChangeApplier
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.game.ChangeGamePhase
import org.example.project.domain.models.game.ChangeGameStateProgress
import org.example.project.domain.models.game.ChangePlayerState
import org.example.project.domain.models.game.GameChange
import org.example.project.domain.models.game.GameStateProgress
import org.example.project.domain.models.game.GameTurnPhase
import org.example.project.domain.models.game.MakeTransaction
import org.example.project.domain.models.game.NextTurnGame
import org.example.project.domain.models.game.PlayerMoved
import org.example.project.domain.models.game.SetPlayerDoubleCount
import org.example.project.domain.models.game.SetPropertyOwner
import org.example.project.domain.models.game.SetRecentDices
import org.example.project.domain.models.game.SetTurnsInJail
import org.example.project.domain.models.game.SetUpgradeLevel
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.Player
import org.example.project.domain.models.player.PlayerState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameChangeApplierTest {
    private lateinit var gameState: GameState
    private lateinit var players: List<Player>

    @BeforeTest
    fun setUp() {
        players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )

        gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
            players = players,
            cells = createField()
        )
    }


    @Test
    fun `Set Recent Dices Game Change Test`() {
        val dices = Pair(2, 4)

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetRecentDices(
                    dices = dices
                )
            )
        )

        assertTrue {
            newGameState == (gameState.copy(lastDices = dices))
        }
    }


    @Test
    fun `Set Upgrade Level Property Street Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetUpgradeLevel(
                    cellIndex = 1,
                    upgradeLevel = 4
                )
            )
        )

        assertTrue {
            (newGameState.cells[1] as StreetCell).propertyStreet.improvementLevel == 4
        }
    }


    @Test
    fun `Set Upgrade Level Not Property Street Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetUpgradeLevel(
                    cellIndex = 0,
                    upgradeLevel = 4
                )
            )
        )

        assertTrue {
            gameState == newGameState
        }
    }


    @Test
    fun `Set Turn In Jail Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetTurnsInJail(
                    playerIndex = 0,
                    turnsInJail = 2
                )
            )
        )

        assertTrue {
            newGameState.players[0].turnsInJail == 2
        }
    }

    @Test
    fun `Change Game State Progress Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangeGameStateProgress(
                    nextStateProgress = GameStateProgress.FINISHED
                )
            )
        )

        assertTrue {
            newGameState == (gameState.copy(gameStateProgress = GameStateProgress.FINISHED))
        }
    }

    @Test
    fun `Next Turn Game Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                NextTurnGame(
                    playerIndexBefore = 0,
                    playerIndexAfter = 1
                )
            )
        )

        assertTrue {
            newGameState == (gameState.copy(playerTurn = 1))
        }
    }


    @Test
    fun `Change Game Phase Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY
                )
            )
        )

        assertTrue {
            newGameState == (gameState.copy(gameTurnPhase = GameTurnPhase.BUY_PROPERTY))
        }
    }

    @Test
    fun `Set Player Double Count Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPlayerDoubleCount(
                    playerIndex = 0,
                    doubleCount = 3
                )
            )
        )

        assertTrue {
            newGameState.players[0].doubleCount == 3
        }
    }


    @Test
    fun `Player Moved Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 5,
                    to = 5
                )
            )
        )

        assertTrue {
            newGameState.players[0].position == 5
        }
    }

    @Test
    fun `Change Player State Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangePlayerState(
                    playerIndex = 1,
                    newState = PlayerState.NOT_IN_GAME
                )
            )
        )

        assertTrue {
            newGameState.players[1].playerState == PlayerState.NOT_IN_GAME
        }
    }

    @Test
    fun `Set Property Owner Property Street Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 1,
                    propertyIndex = 1
                )
            )
        )

        assertTrue {
            (newGameState.cells[1] as StreetCell).propertyStreet.ownerIndex == 1
        }
    }


    @Test
    fun `Set Property Owner Not Property Street Game Change Test`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 1,
                    propertyIndex = 10
                )
            )
        )

        assertTrue {
            gameState == newGameState
        }
    }


    @Test
    fun `Make Transaction Enough Money Game Change Test`() {
        val fromPlayerIndex = 0
        val toPlayerIndex = 1
        val amount = 450

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                MakeTransaction(
                    fromPlayerIndex = fromPlayerIndex,
                    toPlayerIndex = toPlayerIndex,
                    amount = amount
                )
            )
        )

        assertTrue {
            newGameState.players[fromPlayerIndex].balance == (gameState.players[fromPlayerIndex].balance - amount)
        }
        assertTrue {
            newGameState.players[toPlayerIndex].balance == (gameState.players[toPlayerIndex].balance + amount)
        }
    }



    @Test
    fun `Make Transaction Not Enough Money Game Change Test`() {
        val fromPlayerIndex = 0
        val toPlayerIndex = 1
        val amount = 16000

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                MakeTransaction(
                    fromPlayerIndex = fromPlayerIndex,
                    toPlayerIndex = toPlayerIndex,
                    amount = amount
                )
            )
        )

        assertTrue {
            newGameState.players[fromPlayerIndex].balance == 0
        }
        assertTrue {
            newGameState.players[fromPlayerIndex].playerState == PlayerState.NOT_IN_GAME
        }
        assertTrue {
            newGameState.gameTurnPhase == GameTurnPhase.END_TURN
        }
        assertTrue {
            newGameState.players[toPlayerIndex].balance == (gameState.players[toPlayerIndex].balance + gameState.players[fromPlayerIndex].balance)
        }
    }
}