package org.example.project

import androidx.compose.ui.graphics.Color
import org.example.project.UI.screen.game.GameState
import org.example.project.data.GameActionValidator
import org.example.project.data.GameChangeApplier
import org.example.project.data.GameEngine
import org.example.project.data.createField
import org.example.project.data.models.cell.StreetCell
import org.example.project.data.models.game.*
import org.example.project.data.models.player.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameEngineAvalableActionTest {
    @Test
    fun `Buy Upgrade Action Avalable Owns All One Color`() {
        val players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )

        val gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
            players = players,
            cells = createField()
        )

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0,
                    propertyIndex = 1
                ),
                SetPropertyOwner(
                    playerIndex = 0,
                    propertyIndex = 3
                )
            )
        )


        assertTrue { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertTrue { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }

    @Test
    fun `Buy Upgrade Action Not Avalable Not Owns All One Color`() {
        val players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )

        val gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
            players = players,
            cells = createField()
        )

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0,
                    propertyIndex = 1
                ),
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }

    @Test
    fun `Buy Upgrade Action Not Avalable Owns All One Color Not Enough Money`() {
        val players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )

        val gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
            players = players,
            cells = createField()
        )

        val newGameState = GameChangeApplier.applyGameChanges(
            gameState = gameState,
            gameChanges = listOf<GameChange>(
                SetPropertyOwner(
                    playerIndex = 0,
                    propertyIndex = 1
                ),
                SetPropertyOwner(
                    playerIndex = 0,
                    propertyIndex = 3
                ),
                MakeTransaction(
                    fromPlayerIndex = 0,
                    toPlayerIndex = null,
                    amount = 1499
                )
            )
        )


        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(1)) }
        assertFalse { GameActionValidator.isActionAvailable(newGameState, BuyUpgradeAction(3)) }
    }
}