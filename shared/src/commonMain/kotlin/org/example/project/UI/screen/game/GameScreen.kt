package org.example.project.UI.screen.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import monopoly.shared.generated.resources.*
import org.example.project.domain.models.cell.Cell
import org.example.project.domain.models.cell.ChanceCell
import org.example.project.domain.models.cell.CommunityChestCell
import org.example.project.domain.models.cell.FreeParkingCell
import org.example.project.domain.models.cell.GoCell
import org.example.project.domain.models.cell.GoToJailCell
import org.example.project.domain.models.cell.JailCell
import org.example.project.domain.models.cell.RailroadCell
import org.example.project.domain.models.cell.StreetCell
import org.example.project.domain.models.cell.TaxCell
import org.example.project.domain.models.cell.UtilityCell
import org.example.project.domain.models.game.BuyPropertyAction
import org.example.project.domain.models.game.BuyUpgradeAction
import org.example.project.domain.models.game.EndTurnAction
import org.example.project.domain.models.game.GameAction
import org.example.project.domain.models.game.GameState
import org.example.project.domain.models.game.GameStateProgress
import org.example.project.domain.models.game.SellUpgradeAction
import org.example.project.domain.models.game.ThrowDiceAction
import org.example.project.domain.models.player.Player
import org.example.project.utils.Colors
import org.jetbrains.compose.resources.painterResource

@Composable
fun Board(viewModel: GameViewModel, gameId: String) {
    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId = gameId)
    }

    val state by viewModel.state.collectAsState()

    val gameState = state.gameState
    val cellClicked = state.cellClicked
    val availableActions = state.availableActions

    when (gameState.gameStateProgress) {
        GameStateProgress.LOADING -> {
            print("LOAD")
        }

        GameStateProgress.ERROR -> {
            print("ERROR")
        }

        GameStateProgress.IN_PROGRESS -> BoardSuccess(
            gameState = gameState,
            cellClicked = cellClicked,
            availableActions = availableActions,
            viewModel = viewModel
        )

        GameStateProgress.FINISHED -> {
            print("FINISHED")
        }
    }
}


@Composable
fun BoardSuccess(gameState: GameState, cellClicked: Int, availableActions: List<GameAction>, viewModel: GameViewModel) {
    val cells = gameState.cells

    val playersGroup = gameState.players.groupBy { it.position }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val cellWidth = (maxWidth) / 11
        val cellHeight = (maxHeight) / 11

        Column {
            // up
            Row(
                modifier = Modifier
                    .height(cellHeight)
                    .fillMaxWidth()
            ) {
                for (i in 20..30) {
                    Cell(
                        cellId = i,
                        cell = cells[i],
                        players = playersGroup[i],
                        viewModel = viewModel,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight
                    )
                }
            }

            // middle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cellHeight * 9)
            ) {
//            middle-left
                Column(
                    modifier = Modifier
                        .width(cellWidth)
                        .height(cellHeight * 9)
                ) {
                    for (i in 19 downTo 11) {
                        Cell(
                            cellId = i,
                            cell = cells[i],
                            players = playersGroup[i],
                            viewModel = viewModel,
                            cellWidth = cellWidth,
                            cellHeight = cellHeight
                        )
                    }
                }

                // center
                Box(
                    modifier = Modifier
                        .width(cellWidth * 9)
                        .height(cellHeight * 9)
                ) {
                    centerBox(
                        gameState = gameState,
                        cellClicked = cellClicked,
                        availableActions = availableActions,
                        viewModel = viewModel,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight
                    )
                }

                Column(
                    modifier = Modifier
                        .width(cellWidth)
                        .height(cellHeight * 9)
                ) {
                    for (i in 31..39) {
                        Cell(
                            cellId = i,
                            cell = cells[i],
                            players = playersGroup[i],
                            viewModel = viewModel,
                            cellWidth = cellWidth,
                            cellHeight = cellHeight
                        )
                    }
                }
            }

            // down
            Row(
                modifier = Modifier
                    .height(cellHeight)
                    .fillMaxWidth()
            ) {
                for (i in 10 downTo 0) {
                    Cell(
                        cellId = i,
                        cell = cells[i],
                        players = playersGroup[i],
                        viewModel = viewModel,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight
                    )
                }
            }
        }
    }
}

@Composable
fun Cell(cellId: Int, cell: Cell, players: List<Player>?, viewModel: GameViewModel, cellWidth: Dp, cellHeight: Dp) {
    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
            .clickable(
                enabled = true,
                onClick = {
                    viewModel.clickCell(cellId)
                }
            )
    ) {
        when (cell) {
            is GoCell -> GoCell(
                goCell = cell,
                players = players,
                cellWidth = cellWidth,
                cellHeight = cellHeight
            )

            is StreetCell -> StreetCell(
                streetCell = cell,
                players = players,
                cellHeight = cellHeight,
                cellWidth = cellWidth
            )

            is TaxCell -> TaxCell(
                taxCell = cell,
                players = players,
                cellHeight = cellHeight,
                cellWidth = cellWidth
            )

            else -> OtherCell(cell = cell, players = players, cellWidth = cellWidth, cellHeight = cellHeight)
        }
    }
}

@Composable
fun OtherCell(cell: Cell, players: List<Player>?, cellWidth: Dp, cellHeight: Dp) {
    val image = when (cell) {
        is ChanceCell -> Res.drawable.chance
        is CommunityChestCell -> Res.drawable.communityChest
        is FreeParkingCell -> Res.drawable.freeParking
        is GoToJailCell -> Res.drawable.goToJail
        is JailCell -> Res.drawable.jail
        is RailroadCell -> Res.drawable.railway
        is UtilityCell -> Res.drawable.utility
        else -> null
    }

    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = cell.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        DrawPlayers(players = players, cellHeight = cellHeight)

        if (image != null) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                painter = painterResource(image),
                contentDescription = null
            )
        }
    }
}

@Composable
fun StreetCell(streetCell: StreetCell, players: List<Player>?, cellHeight: Dp, cellWidth: Dp) {
    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
    ) {
        Spacer(
            modifier = Modifier
                .background(streetCell.propertyStreet.streetColor)
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = streetCell.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        DrawPlayers(players = players, cellHeight = cellHeight)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = streetCell.propertyStreet.cost.toString(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun GoCell(goCell: GoCell, players: List<Player>?, cellWidth: Dp, cellHeight: Dp) {
    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = goCell.name,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        DrawPlayers(players = players, cellHeight = cellHeight)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = "200K",
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TaxCell(taxCell: TaxCell, players: List<Player>?, cellWidth: Dp, cellHeight: Dp) {
    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = taxCell.name,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        DrawPlayers(players = players, cellHeight = cellHeight)

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            painter = painterResource(Res.drawable.tax),
            contentDescription = null
        )
    }
}


@Composable
fun DrawPlayers(players: List<Player>?, cellHeight: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight / 6),
        horizontalArrangement = Arrangement.Center
    ) {
        if (players != null) {
            for (player in players) {
                Box(
                    modifier = Modifier
                        .size(cellHeight / 6)
                        .border(1.dp, Colors.BLACK)
                        .background(player.color)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        text = player.name,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }
    }
}

@Composable
fun centerBox(
    gameState: GameState,
    cellClicked: Int,
    availableActions: List<GameAction>,
    viewModel: GameViewModel,
    cellWidth: Dp,
    cellHeight: Dp
) {


    val player = gameState.players[gameState.playerTurn]

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = cellHeight)
                .background(player.color),
            text = "Ходит: ${player.name}",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = cellHeight),
            text = "Баланс: ${player.balance}",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = cellHeight),
            text = "Выпало: (${gameState.lastDices.first}, ${gameState.lastDices.second})",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val cell = gameState.cells[cellClicked]

        if (cell is StreetCell) {
            CellStreetDetailed(
                gameState = gameState,
                streetCell = cell,
                cellHeight = cellHeight * 4,
                cellWidth = cellWidth * 4
            )
        } else {
            Cell(
                cellId = cellClicked,
                cell = cell,
                cellHeight = cellHeight * 4,
                cellWidth = cellWidth * 4,
                players = null,
                viewModel = viewModel
            )
        }



        for (action in availableActions) {
            when (action) {
                is BuyPropertyAction -> {
                    Button(
                        modifier = Modifier
                            .width(cellWidth * 2)
                            .height(cellHeight),
                        onClick = {
                            viewModel.sendEvent(action)
                        }
                    ) {
                        Text("КУПИТЬ СОБСТВЕННОСТЬ")
                    }
                }

                is ThrowDiceAction -> {
                    Button(
                        modifier = Modifier
                            .width(cellWidth * 2)
                            .height(cellHeight),
                        onClick = {
                            viewModel.sendEvent(action)
                        }
                    ) {
                        Text("Бросить кубик")
                    }
                }

                is BuyUpgradeAction -> {
                    Button(
                        modifier = Modifier
                            .width(cellWidth * 2)
                            .height(cellHeight),
                        onClick = {
                            viewModel.sendEvent(action)
                        }
                    ) {
                        Text("Купить улучшение")
                    }
                }

                is SellUpgradeAction -> {
                    Button(
                        modifier = Modifier
                            .width(cellWidth * 2)
                            .height(cellHeight),
                        onClick = {
                            viewModel.sendEvent(action)
                        }
                    ) {
                        Text("Продать улучшение")
                    }
                }


                is EndTurnAction -> {
                    Button(
                        modifier = Modifier
                            .width(cellWidth * 2)
                            .height(cellHeight),
                        onClick = {
                            viewModel.sendEvent(action)
                        }
                    ) {
                        Text("Закончить ход")
                    }
                }
            }
        }
    }
}

@Composable
fun CellStreetDetailed(gameState: GameState, streetCell: StreetCell, cellHeight: Dp, cellWidth: Dp) {
    print(gameState)

    Column(
        modifier = Modifier
            .width(cellWidth)
            .height(cellHeight)
            .border(1.dp, Colors.BLACK)
    ) {
        Spacer(
            modifier = Modifier
                .background(streetCell.propertyStreet.streetColor)
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = streetCell.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    if (streetCell.propertyStreet.ownerIndex != null) gameState.players[streetCell.propertyStreet.ownerIndex!!].color
                    else Color.White
                ),
            text = "Владелец: " +
                    if (streetCell.propertyStreet.ownerIndex != null) gameState.players[streetCell.propertyStreet.ownerIndex!!].name
                    else "нет",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier
                .weight(1f)
        ) {
            if (streetCell.propertyStreet.improvementLevel == 5) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    painter = painterResource(Res.drawable.home),
                    colorFilter = ColorFilter.tint(Colors.YELLOW),
                    contentDescription = null
                )
            } else {
                for (i in 0 until streetCell.propertyStreet.improvementLevel) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        painter = painterResource(Res.drawable.home),
                        colorFilter = ColorFilter.tint(Color.Gray),
                        contentDescription = null
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = streetCell.propertyStreet.cost.toString(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}