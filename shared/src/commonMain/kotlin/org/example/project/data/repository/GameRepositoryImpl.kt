package org.example.project.data.repository

import org.example.project.domain.repository.GameRepository
import org.example.project.domain.models.Color
import org.example.project.domain.models.cell.*
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.player.Player
import kotlin.uuid.Uuid

class GameRepositoryImpl: GameRepository {
    private val games: MutableMap<String, GameState> = mutableMapOf()

    override fun createGame(players: List<Player>): String {
        val id = Uuid.random().toString()

        games[id] = GameState(
            isFinished = false,
            players = players,
            cells = createField()
        )

        return id
    }

    override fun getGameById(id: String): GameState {
        return games[id] ?: GameState()
    }
}

fun createField(): List<Cell> {
    return listOf(
        GoCell(),

        StreetCell(
            PropertyStreet(
                name = "Mediterranean Avenue",
                streetColor = Color.BROWN,
                cost = 60,
                rent = listOf(2, 10, 30, 90, 160, 250),
                houseCost = 50
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Baltic Avenue",
                streetColor = Color.BROWN,
                cost = 60,
                rent = listOf(4, 20, 60, 180, 320, 450),
                houseCost = 50
            )
        ),

        TaxCell(
            taxName = "Income Tax",
            taxValue = 200
        ),

        RailroadCell(),

        StreetCell(
            PropertyStreet(
                name = "Oriental Avenue",
                streetColor = Color.BLUE,
                cost = 100,
                rent = listOf(6, 30, 90, 270, 400, 550),
                houseCost = 50
            )
        ),

        ChanceCell(),

        StreetCell(
            PropertyStreet(
                name = "Vermont Avenue",
                streetColor = Color.BLUE,
                cost = 100,
                rent = listOf(6, 30, 90, 270, 400, 550),
                houseCost = 50
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "Connecticut Avenue",
                streetColor = Color.BLUE,
                cost = 120,
                rent = listOf(8, 40, 100, 300, 450, 600),
                houseCost = 50
            )
        ),

        JailCell(),

        StreetCell(
            PropertyStreet(
                name = "St. Charles Place",
                streetColor = Color.VIOLET,
                cost = 140,
                rent = listOf(10, 50, 150, 450, 625, 750),
                houseCost = 100
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "States Avenue",
                streetColor = Color.VIOLET,
                cost = 140,
                rent = listOf(10, 50, 150, 450, 625, 750),
                houseCost = 100
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "Virginia Avenue",
                streetColor = Color.VIOLET,
                cost = 160,
                rent = listOf(12, 60, 180, 500, 700, 900),
                houseCost = 100
            )
        ),

        RailroadCell(),

        StreetCell(
            PropertyStreet(
                name = "St. James Place",
                streetColor = Color.ORANGE,
                cost = 180,
                rent = listOf(14, 70, 200, 550, 750, 950),
                houseCost = 100
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Tennessee Avenue",
                streetColor = Color.ORANGE,
                cost = 180,
                rent = listOf(14, 70, 200, 550, 750, 950),
                houseCost = 100
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "New York Avenue",
                streetColor = Color.ORANGE,
                cost = 200,
                rent = listOf(16, 80, 220, 600, 800, 1000),
                houseCost = 100
            )
        ),

        FreeParkingCell(),

        StreetCell(
            PropertyStreet(
                name = "Kentucky Avenue",
                streetColor = Color.RED,
                cost = 220,
                rent = listOf(18, 90, 250, 700, 875, 1050),
                houseCost = 150
            )
        ),

        ChanceCell(),

        StreetCell(
            PropertyStreet(
                name = "Indiana Avenue",
                streetColor = Color.RED,
                cost = 220,
                rent = listOf(18, 90, 250, 700, 875, 1050),
                houseCost = 150
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "Illinois Avenue",
                streetColor = Color.RED,
                cost = 240,
                rent = listOf(20, 100, 300, 750, 925, 1100),
                houseCost = 150
            )
        ),

        RailroadCell(),

        StreetCell(
            PropertyStreet(
                name = "Atlantic Avenue",
                streetColor = Color.YELLOW,
                cost = 260,
                rent = listOf(22, 110, 330, 800, 975, 1150),
                houseCost = 150
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "Ventnor Avenue",
                streetColor = Color.YELLOW,
                cost = 260,
                rent = listOf(22, 110, 330, 800, 975, 1150),
                houseCost = 150
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "Marvin Gardens",
                streetColor = Color.YELLOW,
                cost = 280,
                rent = listOf(24, 120, 360, 850, 1025, 1200),
                houseCost = 150
            )
        ),

        GoToJailCell(),

        StreetCell(
            PropertyStreet(
                name = "Pacific Avenue",
                streetColor = Color.GREEN,
                cost = 300,
                rent = listOf(26, 130, 390, 900, 1100, 1275),
                houseCost = 200
            )
        ),

        StreetCell(
            PropertyStreet(
                name = "North Carolina Avenue",
                streetColor = Color.GREEN,
                cost = 300,
                rent = listOf(26, 130, 390, 900, 1100, 1275),
                houseCost = 200
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Pennsylvania Avenue",
                streetColor = Color.GREEN,
                cost = 320,
                rent = listOf(28, 150, 450, 1000, 1200, 1400),
                houseCost = 200
            )
        ),

        RailroadCell(),

        ChanceCell(),

        StreetCell(
            PropertyStreet(
                name = "Park Place",
                streetColor = Color.DARK_BLUE,
                cost = 350,
                rent = listOf(35, 175, 500, 1100, 1300, 1500),
                houseCost = 200
            )
        ),

        TaxCell(
            taxName = "Luxury Tax",
            taxValue = 100
        ),

        StreetCell(
            PropertyStreet(
                name = "Boardwalk",
                streetColor = Color.DARK_BLUE,
                cost = 400,
                rent = listOf(50, 200, 600, 1400, 1700, 2000),
                houseCost = 200
            )
        )
    )
}

