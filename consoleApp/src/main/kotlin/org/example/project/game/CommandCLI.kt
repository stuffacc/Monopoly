package org.example.project.game

import org.example.project.domain.models.game.GameAction

sealed interface CommandCLI

data class SelectCellCommand(val cellId: Int): CommandCLI
data class GameActionCommand(val action: GameAction): CommandCLI
data class InvalidCommand(val message: String): CommandCLI