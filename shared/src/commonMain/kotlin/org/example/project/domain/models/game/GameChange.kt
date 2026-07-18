package org.example.project.domain.models.game

import org.example.project.domain.models.player.PlayerState

sealed class GameChange

data class PlayerMoved(
    val playerIndex: Int,
    val from: Int,
    val value: Int,
    val to: Int
): GameChange()

data class ChangePlayerState(
    val playerIndex: Int,
    val newState: PlayerState
) : GameChange()

data class SetPropertyOwner(
    val playerIndex: Int?,
    val propertyIndex: Int
) : GameChange()

data class ChangeGamePhase(
    val previousPhase: GameTurnPhase,
    val nextPhase: GameTurnPhase
) : GameChange()

data class SetPlayerDoubleCount(
    val playerIndex: Int,
    val doubleCount: Int
) : GameChange()

data class NextTurnGame(
    val playerIndexBefore: Int,
    val playerIndexAfter: Int
): GameChange()

data class ChangeGameStateProgress(
    val nextStateProgress: GameStateProgress
) : GameChange()

data class SetTurnsInJail(
    val playerIndex: Int,
    val turnsInJail: Int
) : GameChange()


data class SetUpgradeLevel(
    val cellIndex: Int,
    val upgradeLevel: Int
) : GameChange()


data class MakeTransaction(
    val fromPlayerIndex: Int?,
    val toPlayerIndex: Int?,
    val amount: Int
): GameChange()

data class SetRecentDices(
    val dices: Pair<Int, Int>
): GameChange()

