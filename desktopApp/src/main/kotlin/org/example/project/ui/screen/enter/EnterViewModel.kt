package org.example.project.ui.screen.enter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.domain.models.player.SendPlayer
import org.example.project.domain.usecase.CreateGameUsecase

class EnterViewModel(
    private val createGameUsecase: CreateGameUsecase
): ViewModel() {
    private val _state = MutableStateFlow(EnterScreenState())

    val state = _state.asStateFlow()

    fun changeName(value: String) {
        _state.update {
            it.copy(
                name = value
            )
        }
    }

    fun addPlayer() {
        val name = state.value.name.trim()

        if (name.isEmpty()) {
            return
        }

        _state.update {
            it.copy(
                sendPlayers = it.sendPlayers + SendPlayer(name),
                name = ""
            )
        }
    }

    fun removePlayer(indexPlayer: Int) {
        _state.update {
            it.copy(
                sendPlayers = it.sendPlayers
                    .filterIndexed { index, player -> indexPlayer != index }
            )
        }
    }

    fun startGame(): String {
        val gameId = createGameUsecase.execute(sendPlayers = state.value.sendPlayers)

        _state.update {
            it.copy(
                gameId = gameId
            )
        }

        return gameId
    }
}