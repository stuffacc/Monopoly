package org.example.project

import androidx.compose.ui.graphics.Color
import org.example.project.domain.models.game.GameState
import org.example.project.domain.engine.GameActionValidator
import org.example.project.domain.engine.GameChangeApplier
import org.example.project.data.createField
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.ChangeGamePhase
import org.example.project.domain.models.game.ChangePlayerState
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameChange
import org.example.project.domain.models.game.GameStateProgress
import org.example.project.domain.models.game.GameTurnPhase
import org.example.project.domain.models.game.MakeTransaction
import org.example.project.domain.models.game.PlayerMoved
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.SetPropertyOwner
import org.example.project.domain.models.game.SetUpgradeLevel
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.Player
import org.example.project.domain.models.player.PlayerState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameActionValidatorTest {
    private lateinit var gameState: GameState
    private lateinit var players: List<Player>

    @BeforeTest
    fun setUp() {
        players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )


        gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS, players = players, cells = createField()
        )
    }


    // Throw Dice Tests
    @Test
    fun `Throw Dice Not Available If Player Is Not In Game`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangePlayerState(
                    playerIndex = 0,
                    newState = PlayerState.NOT_IN_GAME
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, ThrowDiceAction(0, 0)) }
    }

    @Test
    fun `Throw Dice Not Available If Not Start Turn Phase`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.END_TURN,
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, ThrowDiceAction(0, 0)) }
    }

    // Buy Property Action Test
    @Test
    fun `Buy Property Action Not Avalable Not Buy Property Phase`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 3,
                    to = 3
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.BUY_PROPERTY,
                    nextPhase = GameTurnPhase.END_TURN,
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(3)) }
    }


    @Test
    fun `Buy Property Action Not Avalable Player Not On The Cell`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 3,
                    to = 3
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(1)) }
    }

    @Test
    fun `Buy Property Action Not Avalable Cell Has A Owner`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 1,
                    propertyIndex = 3
                ),
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 3,
                    to = 3
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(3)) }
    }


    @Test
    fun `Buy Property Action Not Avalable Not Enough Money`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 3,
                    to = 3
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                ),
                MakeTransaction(
                    fromPlayerIndex = 0,
                    toPlayerIndex = null,
                    amount = 1499
                )
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(3)) }
    }

    @Test
    fun `Buy Property Action Not Avalable Not Property Cell`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 2,
                    to = 2
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                ),
            )
        )

        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(2)) }
    }


    @Test
    fun `Buy Property Action Avalable Player On The Cell, Enough Money, Cell Has Not Owner, Is Property Cell`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                PlayerMoved(
                    playerIndex = 0,
                    from = 0,
                    value = 3,
                    to = 3
                ),
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                )
            )
        )

        assertTrue { GameActionValidator.isActionAvailable(newGameState, BuyPropertyAction(3)) }
    }


    // Buy Upgrade Tests
    @Test
    fun `Buy Upgrade Action Not Avalable Not Owns All One Color`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ),
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }

    @Test
    fun `Buy Upgrade Action Not Avalable Owns All One Color Not Enough Money`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState, gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ), SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 3
                ), MakeTransaction(
                    fromPlayerIndex = 0, toPlayerIndex = null, amount = 1499
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }


    @Test
    fun `Buy Upgrade Action Not Avalable Not Street Cell`() {
        assertFalse { GameActionValidator.isActionAvailable(gameState, BuyUpgradeAction(0)) }
        assertFalse { GameActionValidator.isActionAvailable(gameState, BuyUpgradeAction(2)) }
    }


    @Test
    fun `Buy Upgrade Action Not Avalable Level is Maximum`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState, gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ), SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 3
                ), SetUpgradeLevel(
                    cellIndex = 1, upgradeLevel = 5
                ), SetUpgradeLevel(
                    cellIndex = 3, upgradeLevel = 5
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }

    @Test
    fun `Buy Upgrade Action Not Avalable If Not Min Level`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState, gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ), SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 3
                ), SetUpgradeLevel(
                    cellIndex = 1, upgradeLevel = 3
                ), SetUpgradeLevel(
                    cellIndex = 3, upgradeLevel = 2
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertTrue { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }


    // Sell Upgrade Tests

    @Test
    fun `Sell Upgrade Action Not Avalable Not Owns All One Color`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState, gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ),
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(3)) }
    }


    @Test
    fun `Sell Upgrade Action Not Avalable Not Street Cell`() {
        assertFalse { GameActionValidator.isActionAvailable(gameState, SellUpgradeAction(0)) }
        assertFalse { GameActionValidator.isActionAvailable(gameState, SellUpgradeAction(2)) }
    }

    @Test
    fun `Sell Upgrade Action Not Avalable Level is Minimum`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState, gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ), SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 3
                ), SetUpgradeLevel(
                    cellIndex = 1, upgradeLevel = 0
                ), SetUpgradeLevel(
                    cellIndex = 3, upgradeLevel = 0
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(3)) }
    }

    @Test
    fun `Sell Upgrade Action Not Avalable If Not Max Level`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 1
                ), SetPropertyOwner(
                    playerIndex = 0, propertyIndex = 3
                ), SetUpgradeLevel(
                    cellIndex = 1, upgradeLevel = 2
                ), SetUpgradeLevel(
                    cellIndex = 3, upgradeLevel = 3
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(1)) }
        assertTrue { GameActionValidator.isActionAvailable(newGameState, SellUpgradeAction(3)) }
    }

    @Test
    fun `End Turn Action Available Phase Is Buy Property`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.BUY_PROPERTY,
                )
            )
        )

        assertTrue { GameActionValidator.isActionAvailable(newGameState, EndTurnAction) }
    }

    @Test
    fun `End Turn Action Available Phase Is End Turn`() {
        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                ChangeGamePhase(
                    previousPhase = GameTurnPhase.START_TURN,
                    nextPhase = GameTurnPhase.END_TURN,
                )
            )
        )

        assertTrue { GameActionValidator.isActionAvailable(newGameState, EndTurnAction) }
    }

    @Test
    fun `End Turn Action Not Available Phase Is Not Buy Property Or End Turn`() {
        assertFalse { GameActionValidator.isActionAvailable(gameState, EndTurnAction) }
    }
}