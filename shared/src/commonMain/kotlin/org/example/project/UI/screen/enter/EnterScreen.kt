package org.example.project.UI.screen.enter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.utils.colors

@Composable
fun EnterScreen(
    viewModel: EnterViewModel,
    onNavigateToGame: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            TextField(
                modifier = Modifier
                    .weight(5f),
                value = state.name,
                onValueChange = {
                    viewModel.changeName(it)
                }
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Button(
                modifier = Modifier
                    .weight(2f),
                onClick = viewModel::addPlayer,
                enabled = state.players.size < 6
            ) {
                Text(
                    text = "Добавить игрока",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

        }

        Spacer(modifier = Modifier
            .padding(16.dp)
        )

        LazyColumn (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(
                items = state.players,
                key = { _, it -> it.id }
            )
            { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(5f),
                        text = item.name
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 16.dp, bottom = 8.dp)
                            .background(colors[index])
                    )

                    Button(
                        onClick = {
                            viewModel.removePlayer(item.id)
                        },
                        modifier = Modifier
                            .weight(2f)
                    ) {
                        Text(
                            text = "Убрать",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier
            .padding(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.players.size >= 2,
            onClick = {
                val id = viewModel.startGame()
                onNavigateToGame(id)
            },
        ) {
            Text(
                text = "Начать игру",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }

    }
}