package org.example.project.UI.screen.game

import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.GameStateProgress
import kotlin.collections.emptyList

data class GameScreenState (
    val gameState: GameState = GameState(gameStateProgress = GameStateProgress.LOADING),
    val cellClicked: Int = 0,
    val availableActions: List<GameAction> = emptyList<GameAction>()
)