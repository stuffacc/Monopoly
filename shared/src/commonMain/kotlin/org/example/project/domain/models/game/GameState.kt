package org.example.project.domain.models.game

import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.player.Player

data class GameState(
    val gameStateProgress: GameStateProgress = GameStateProgress.ERROR,
    val players: List<Player> = emptyList(),
    val cells: List<Cell> = emptyList(),
    val turnCount: Int = 0,
    val playerTurn: Int = 0,
    val gameTurnPhase: GameTurnPhase = GameTurnPhase.START_TURN,
    val lastDices: Pair<Int, Int> = Pair(0, 0),
)