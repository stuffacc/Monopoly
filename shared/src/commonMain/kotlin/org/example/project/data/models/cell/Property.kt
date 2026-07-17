package org.example.project.data.models.cell

import androidx.compose.ui.graphics.Color
import org.example.project.UI.screen.game.GameState
import org.example.project.data.models.player.Player
import kotlin.math.max
import kotlin.math.min


sealed class Property(
    open val name: String,
    open val cost: Int,
    open val ownerIndex: Int? = null
)

data class PropertyStreet(
    override val name: String,
    override val cost: Int,
    override val ownerIndex: Int? = null,
    val streetColor: Color,
    val improvementLevel: Int = 0
) : Property(name = name, cost = cost, ownerIndex = ownerIndex)