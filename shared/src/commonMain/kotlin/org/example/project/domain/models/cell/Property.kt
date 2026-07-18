package org.example.project.domain.models.cell

import androidx.compose.ui.graphics.Color

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