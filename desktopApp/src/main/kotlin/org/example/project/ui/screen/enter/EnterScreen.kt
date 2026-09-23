package org.example.project.ui.screen.enter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                maxLines = 1,
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
                enabled = state.sendPlayers.size < 6
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
                items = state.sendPlayers
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

                    Button(
                        onClick = {
                            viewModel.removePlayer(index)
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
            enabled = state.sendPlayers.size >= 2,
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