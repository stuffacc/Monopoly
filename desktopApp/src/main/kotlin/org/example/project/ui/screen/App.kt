package org.example.project.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.project.ui.navigation.Screen
import org.example.project.ui.screen.enter.EnterScreen
import org.example.project.ui.screen.enter.EnterViewModel
import org.example.project.ui.screen.game.Board
import org.example.project.ui.screen.game.GameViewModel
import org.example.project.data.RandomValueGeneratorImpl
import org.example.project.data.repository.GameRepositoryImpl
import org.example.project.data.repository.PlayerRepositoryImpl
import org.example.project.domain.usecase.CreateGameUsecase
import org.example.project.domain.usecase.GetAvailableActionUsecase
import org.example.project.domain.usecase.LoadGameUsecase
import org.example.project.domain.usecase.SendActionUsecase


@Composable
fun App() {
    val navController = rememberNavController()

    val gameRepository = remember { GameRepositoryImpl() }
    val playerRepository = remember { PlayerRepositoryImpl() }
    val randomValueGenerator = remember { RandomValueGeneratorImpl() }

    val sendActionUsecase = remember { SendActionUsecase(randomValueGenerator) }
    val getAvailableActionUsecase = remember { GetAvailableActionUsecase() }
    val createGameUsecase = remember { CreateGameUsecase(gameRepository, playerRepository) }
    val loadGameUsecase = remember { LoadGameUsecase(gameRepository) }


    NavHost(
        navController = navController,
        startDestination = Screen.EnterScreen.route
    ) {

        composable(
            route = Screen.EnterScreen.route,
        ) {
            val viewModel = remember {
                EnterViewModel(createGameUsecase = createGameUsecase)
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
                GameViewModel(
                    loadGameUsecase = loadGameUsecase,
                    sendActionUsecase = sendActionUsecase,
                    getAvailableActionUsecase = getAvailableActionUsecase
                )
            }

            val gameId = it.savedStateHandle.get<String>("gameId") ?: return@composable

            Board(viewModel = viewModel, gameId = gameId)
        }
    }
}