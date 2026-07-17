package org.example.project.UI.screen.game

import org.example.project.data.models.cell.Cell
import org.example.project.data.models.player.Player
import org.example.project.data.models.game.GameStateProgress
import org.example.project.data.models.game.GameTurnPhase

data class GameState(
    val gameStateProgress: GameStateProgress = GameStateProgress.ERROR,
    val players: List<Player> = emptyList(),
    val cells: List<Cell> = emptyList(),
    val turnCount: Int = 0,
    val playerTurn: Int = 0,
    val gameTurnPhase: GameTurnPhase = GameTurnPhase.START_TURN,
    val lastDices: Pair<Int, Int> = Pair(0, 0),

    // TODO: пока так, лучше везде val сделать, тогда будет без этого
    val forceUpdate: Int = 0
)