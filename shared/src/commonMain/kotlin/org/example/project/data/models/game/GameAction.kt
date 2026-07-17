package org.example.project.data.models.game

sealed class GameAction

class ThrowDiceAction : GameAction() {
    // Для тестов
    override fun equals(other: Any?): Boolean {
        other?.let {
            return this::class == other::class
        }

        return false
    }

    override fun hashCode(): Int {
        return 0
    }
}
data class BuyPropertyAction(val cellId: Int) : GameAction()
data class BuyUpgradeAction(val cellId: Int) : GameAction()
data class SellUpgradeAction(val cellId: Int) : GameAction()

class EndTurnAction : GameAction() {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return this::class == other::class
        }

        return false
    }

    override fun hashCode(): Int {
        return 0
    }
}