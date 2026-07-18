package org.example.project.data

import org.example.project.domain.GameRepository
import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.cell.ChanceCell
import org.example.project.domain.models.cell.CommunityChestCell
import org.example.project.domain.models.cell.FreeParkingCell
import org.example.project.domain.models.cell.GoCell
import org.example.project.domain.models.cell.GoToJailCell
import org.example.project.domain.models.cell.JailCell
import org.example.project.domain.models.cell.PropertyStreet
import org.example.project.domain.models.cell.RailroadCell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.cell.TaxCell
import org.example.project.domain.models.cell.UtilityCell
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.GameStateProgress
import org.example.project.domain.models.player.Player
import org.example.project.utils.Colors
import kotlin.uuid.Uuid

class GameRepositoryImpl: GameRepository {
    private val games: MutableMap<String, GameState> = mutableMapOf()

    override fun createGame(players: List<Player>): String {
        val id = Uuid.random().toString()

        games[id] = GameState(
            gameStateProgress = GameStateProgress.IN_PROGRESS,
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
                streetColor = Colors.BROWN,
                cost = 60,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Baltic Avenue",
                streetColor = Colors.BROWN,
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
                streetColor = Colors.BLUE,
                cost = 100,
            )
        ),

        ChanceCell(),

        StreetCell(
            PropertyStreet(
                name = "Vermont Avenue",
                streetColor = Colors.BLUE,
                cost = 100,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Connecticut Avenue",
                streetColor = Colors.BLUE,
                cost = 120,
            )
        ),

        JailCell(),


        StreetCell(
            PropertyStreet(
                name = "St. Charles Place",
                streetColor = Colors.VIOLET,
                cost = 140,
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "States Avenue",
                streetColor = Colors.VIOLET,
                cost = 140,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Virginia Avenue",
                streetColor = Colors.VIOLET,
                cost = 160,
            )
        ),

        RailroadCell(),


        StreetCell(
            PropertyStreet(
                name = "St. James Place",
                streetColor = Colors.ORANGE,
                cost = 180,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Tennessee Avenue",
                streetColor = Colors.ORANGE,
                cost = 180,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "New York Avenue",
                streetColor = Colors.ORANGE,
                cost = 200,
            )
        ),

        FreeParkingCell(),


        StreetCell(
            PropertyStreet(
                name = "Kentucky Avenue",
                streetColor = Colors.RED,
                cost = 220,
            )
        ),
        ChanceCell(),
        StreetCell(
            PropertyStreet(
                name = "Indiana Avenue",
                streetColor = Colors.RED,
                cost = 220,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Illinois Avenue",
                streetColor = Colors.RED,
                cost = 240,
            )
        ),
        RailroadCell(),


        StreetCell(
            PropertyStreet(
                name = "Atlantic Avenue",
                streetColor = Colors.YELLOW,
                cost = 260,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "Ventnor Avenue",
                streetColor = Colors.YELLOW,
                cost = 260,
            )
        ),

        UtilityCell(),

        StreetCell(
            PropertyStreet(
                name = "Marvin Gardens",
                streetColor = Colors.YELLOW,
                cost = 280,
            )
        ),

        GoToJailCell(),


        StreetCell(
            PropertyStreet(
                name = "Pacific Avenue",
                streetColor = Colors.GREEN,
                cost = 300,
            )
        ),
        StreetCell(
            PropertyStreet(
                name = "North Carolina Avenue",
                streetColor = Colors.GREEN,
                cost = 300,
            )
        ),

        CommunityChestCell(),

        StreetCell(
            PropertyStreet(
                name = "Pennsylvania Avenue",
                streetColor = Colors.GREEN,
                cost = 320,
            )
        ),

        RailroadCell(),

        ChanceCell(),


        StreetCell(
            PropertyStreet(
                name = "Park Place",
                streetColor = Colors.DARK_BLUE,
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
                streetColor = Colors.DARK_BLUE,
                cost = 400,
            )
        )
    )
}

