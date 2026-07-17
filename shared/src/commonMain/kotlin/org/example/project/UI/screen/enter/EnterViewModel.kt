package org.example.project.UI.screen.enter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.data.GameRepository
import org.example.project.utils.colors
import org.example.project.data.models.player.Player
import kotlin.uuid.Uuid

class EnterViewModel(
    private val gameRepository: GameRepository
): ViewModel() {
    private val _state = MutableStateFlow(EnterState())

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

        state.value.players

        _state.update {
            it.copy(
                name = "",
                players = it.players.mapIndexed { index, player ->
                    player.copy(
                        color = colors[index]
                    )
                } + Player(
                    id = Uuid.random().toString(),
                    name = name,
                    color = colors[it.players.size]
                )
            )
        }
    }

    fun removePlayer(id: String) {
        _state.update {
            it.copy(
                players = it.players
                    .filterIndexed { _, player -> player.id != id }
                    .mapIndexed { index, player ->
                        player.copy(
                            color = colors[index]
                        )
                    }
            )
        }
    }

    fun startGame(): String {
        val id = gameRepository.createNewGame(players = state.value.players)

        _state.update {
            it.copy(
                gameId = id
            )
        }

        return id
    }
}