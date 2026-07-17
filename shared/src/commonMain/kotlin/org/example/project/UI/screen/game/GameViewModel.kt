package org.example.project.UI.screen.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.data.GameEngine
import org.example.project.data.GameRepository
import org.example.project.data.models.game.BuyUpgradeAction
import org.example.project.data.models.game.GameAction
import org.example.project.data.models.game.GameStateProgress
import org.example.project.data.models.game.SellUpgradeAction

class GameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow<GameState>(
        GameState(gameStateProgress = GameStateProgress.LOADING)
    )
    private val _stateClick = MutableStateFlow<Int>(0)
    private val _stateAvailableActions = MutableStateFlow(emptyList<GameAction>())

    val state = _state.asStateFlow()
    val stateClick = _stateClick.asStateFlow()
    val stateAvailableActions = _stateAvailableActions.asStateFlow()

    fun loadGame(gameId: String) {
        _state.update {
            gameRepository.getGame(id = gameId)
        }

        _stateClick.update {
            state.value.players[state.value.playerTurn].position
        }

        _stateAvailableActions.update {
            GameEngine.getAvailableActions(
                gameState = state.value,
                cellId = stateClick.value
            )
        }
    }

    fun sendEvent(gameAction: GameAction) {
        val gameState = GameEngine.handle(
            gameState = state.value,
            gameAction = gameAction,
        )

        _state.update {
            gameState
        }


        if ((gameAction !is BuyUpgradeAction) && (gameAction !is SellUpgradeAction)) {
            _stateClick.update {
                gameState.players[gameState.playerTurn].position
            }
        }

        _stateAvailableActions.update {
            GameEngine.getAvailableActions(
                gameState = gameState,
                cellId = stateClick.value
            )
        }
    }

    fun clickCell(cellId: Int) {
        _stateClick.update {
            cellId
        }

        _stateAvailableActions.update {
            GameEngine.getAvailableActions(
                gameState = state.value,
                cellId = cellId
            )
        }
    }
}