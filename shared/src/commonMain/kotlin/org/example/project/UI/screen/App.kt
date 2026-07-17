package org.example.project.UI.screen

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.project.data.GameRepository
import org.example.project.UI.navigation.Screen
import org.example.project.UI.screen.enter.EnterScreen
import org.example.project.UI.screen.enter.EnterViewModel
import org.example.project.UI.screen.game.Board
import org.example.project.UI.screen.game.GameViewModel

@Preview
@Composable
fun App() {
    val navController = rememberNavController()
    val gameRepository = remember { GameRepository() }


    NavHost(
        navController = navController,
        startDestination = Screen.EnterScreen.route
    ) {

        composable(
            route = Screen.EnterScreen.route,
        ) {
            val viewModel = remember {
                EnterViewModel(gameRepository = gameRepository)
            }
            EnterScreen(
                viewModel = viewModel,
                onNavigateToGame = { gameId ->
                    navController.navigate("game/$gameId") {
                        popUpTo(Screen.EnterScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.GameScreen.route,
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel = remember {
                GameViewModel(gameRepository = gameRepository)
            }

            val gameId = it.savedStateHandle.get<String>("gameId") ?: return@composable

            Board(viewModel = viewModel, gameId = gameId)
        }
    }
}