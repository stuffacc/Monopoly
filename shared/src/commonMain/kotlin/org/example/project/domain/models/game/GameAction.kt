package org.example.project.domain.models.game

sealed class GameAction

data class ThrowDiceAction(
    val dice1: Int,
    val dice2: Int
) : GameAction()

data class BuyPropertyAction(val cellId: Int) : GameAction()
data class BuyUpgradeAction(val cellId: Int) : GameAction()
data class SellUpgradeAction(val cellId: Int) : GameAction()

data object EndTurnAction : GameAction()