package org.example.project.UI.screen.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.domain.GameRepository
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.usecase.GetAvailableActionUsecase
import org.example.project.domain.usecase.SendActionUsecase

class GameViewModel(
    private val gameRepository: GameRepository,
    private val sendActionUsecase: SendActionUsecase,
    private val getAvailableActionUsecase: GetAvailableActionUsecase,
) : ViewModel() {
    private val _state = MutableStateFlow<GameScreenState>(
        GameScreenState()
    )

    val state = _state.asStateFlow()

    fun loadGame(gameId: String) {
        val gameState = gameRepository.getGameById(id = gameId)
        val cellClicked = gameState.players[gameState.playerTurn].position
        val availableActions = getAvailableActionUsecase.execute(
            gameState = gameState,
            cellId = cellClicked
        )

        _state.update {
            it.copy(
                gameState = gameState,
                cellClicked = cellClicked,
                availableActions = availableActions
            )
        }
    }

    fun sendEvent(gameAction: GameAction) {
        val gameState = sendActionUsecase.execute(
            gameState = state.value.gameState,
            gameAction = gameAction,
        )

        val updatedClickCell =
            if ((gameAction !is BuyUpgradeAction) && (gameAction !is SellUpgradeAction)) gameState.players[gameState.playerTurn].position
            else _state.value.cellClicked

        val availableActions = getAvailableActionUsecase.execute(
            gameState = gameState,
            cellId = updatedClickCell
        )

        _state.update {
            it.copy(
                gameState = gameState,
                cellClicked = updatedClickCell,
                availableActions = availableActions
            )
        }
    }

    fun clickCell(cellId: Int) {
        val availableActions = getAvailableActionUsecase.execute(
            gameState = state.value.gameState,
            cellId = cellId
        )

        _state.update {
            it.copy(
                cellClicked = cellId,
                availableActions = availableActions
            )
        }
    }
}