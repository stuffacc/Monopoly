package org.example.project.UI.navigation

sealed class Screen(val route: String) {
    data object EnterScreen: Screen(route = "enter")
    data object GameScreen: Screen(route = "game/{gameId}")
}