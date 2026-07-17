package org.example.project

import androidx.compose.ui.graphics.Color
import org.example.project.UI.screen.game.GameState
import org.example.project.data.GameEngine
import org.example.project.data.createField
import org.example.project.data.models.cell.StreetCell
import org.example.project.data.models.game.*
import org.example.project.data.models.player.Player
import kotlin.test.Test
import kotlin.test.assertEquals

class GameEngineAvalableActionTest {

    // Надо тестировать методы внутри getAvailableActions, но они приватны. Может сделать не как object
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

        val streetCell1 = gameState.cells[1] as StreetCell
        val streetCell2 = gameState.cells[3] as StreetCell

        streetCell1.propertyStreet.owner = players[0]
        streetCell2.propertyStreet.owner = players[0]

        val actual = GameEngine.getAvailableActions(gameState, cellId = 1)

        val expected = listOf<GameAction>(
            BuyUpgradeAction(cellId = 1),
            ThrowDiceAction()
        )

        assertEquals(
            expected, actual
        )
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

        val streetCell1 = gameState.cells[1] as StreetCell

        streetCell1.propertyStreet.owner = players[0]

        val actual = GameEngine.getAvailableActions(gameState, cellId = 1)

        val expected = listOf<GameAction>(
            ThrowDiceAction()
        )

        assertEquals(
            expected, actual
        )
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

        val streetCell1 = gameState.cells[1] as StreetCell
        val streetCell2 = gameState.cells[3] as StreetCell

        streetCell1.propertyStreet.owner = players[0]
        streetCell2.propertyStreet.owner = players[0]

        players[0].balance = 10

        val actual = GameEngine.getAvailableActions(gameState, cellId = 1)

        val expected = listOf<GameAction>(
            ThrowDiceAction()
        )

        assertEquals(
            expected, actual
        )
    }


    @Test
    fun `Buy Upgrade Action Not Avalable Owns All One Color No Min LevelUpgrade`() {
        val players = listOf(
            Player(id = "1", name = "player1", color = Color.Blue),
            Player(id = "2", name = "player2", color = Color.Red),
        )

        val gameState = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
            players = players,
            cells = createField()
        )

        val streetCell1 = gameState.cells[1] as StreetCell
        val streetCell2 = gameState.cells[3] as StreetCell

        streetCell1.propertyStreet.owner = players[0]
        streetCell2.propertyStreet.owner = players[0]

        streetCell1.propertyStreet.improvementLevel = 1
        streetCell2.propertyStreet.improvementLevel = 0

        players[0].balance = 10

        val actual = GameEngine.getAvailableActions(gameState, cellId = 1)

        val expected = listOf<GameAction>(
            SellUpgradeAction(cellId = 1),
            ThrowDiceAction()
        )

        assertEquals(
            expected, actual
        )
    }
}