package org.example.project.ui.screen.game

import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState

data class GameScreenState (
    val isLoading: Boolean = true,
    val gameState: GameState = GameState(),
    val cellClicked: Int = 0,
    val availableActions: List<GameAction> = emptyList<GameAction>()
)