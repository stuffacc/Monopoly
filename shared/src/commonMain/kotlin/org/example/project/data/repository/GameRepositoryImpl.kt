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
    return arrayListOf(
        GoCell(),

        StreetCell(
            PropertyStreet(
                name = "Mediter-ranean Avenue",
                streetColor = Color.BROWN,
                cost = 60,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Baltic Avenue",
                streetColor = Color.BROWN,
                cost = 60,
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
            )
        ),

        ChanceCell(),

        StreetCell(
            PropertyStreet(
                name = "Vermont Avenue",
                streetColor = Color.BLUE,
                cost = 100,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Connecticut Avenue",
                streetColor = Color.BLUE,
                cost = 120,
            )
        ),

        JailCell(),


        StreetCell(
            PropertyStreet(
                name = "St. Charles Place",
                streetColor = Color.VIOLET,
                cost = 140,
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "States Avenue",
                streetColor = Color.VIOLET,
                cost = 140,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Virginia Avenue",
                streetColor = Color.VIOLET,
                cost = 160,
            )
        ),

        RailroadCell(),


        StreetCell(
            PropertyStreet(
                name = "St. James Place",
                streetColor = Color.ORANGE,
                cost = 180,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Tennessee Avenue",
                streetColor = Color.ORANGE,
                cost = 180,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "New York Avenue",
                streetColor = Color.ORANGE,
                cost = 200,
            )
        ),

        FreeParkingCell(),


        StreetCell(
            PropertyStreet(
                name = "Kentucky Avenue",
                streetColor = Color.RED,
                cost = 220,
            )
        ),
        ChanceCell(),
        StreetCell(
            PropertyStreet(
                name = "Indiana Avenue",
                streetColor = Color.RED,
                cost = 220,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Illinois Avenue",
                streetColor = Color.RED,
                cost = 240,
            )
        ),
        RailroadCell(),


        StreetCell(
            PropertyStreet(
                name = "Atlantic Avenue",
                streetColor = Color.YELLOW,
                cost = 260,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Ventnor Avenue",
                streetColor = Color.YELLOW,
                cost = 260,
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "Marvin Gardens",
                streetColor = Color.YELLOW,
                cost = 280,
            )
        ),

        GoToJailCell(),


        StreetCell(
            PropertyStreet(
                name = "Pacific Avenue",
                streetColor = Color.GREEN,
                cost = 300,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "North Carolina Avenue",
                streetColor = Color.GREEN,
                cost = 300,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Pennsylvania Avenue",
                streetColor = Color.GREEN,
                cost = 320,
            )
        ),

        RailroadCell(),

        ChanceCell(),


        StreetCell(
            PropertyStreet(
                name = "Park Place",
                streetColor = Color.DARK_BLUE,
                cost = 350,
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
            )
        )
    )
}

