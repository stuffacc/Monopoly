package org.example.project

import org.example.project.domain.repository.GameRepository
import org.example.project.domain.repository.PlayerRepository
import org.example.project.domain.repository.RandomValueGenerator
import org.example.project.domain.usecase.CreateGameUsecase
import org.example.project.domain.usecase.GetAvailableActionUsecase
import org.example.project.domain.usecase.LoadGameUsecase
import org.example.project.domain.usecase.SendActionUsecase
import org.example.project.enter.EnterController
import org.example.project.game.GameController

class ApplicationController(
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository,
    private val randomValueGenerator: RandomValueGenerator
) {
    private val enterController = EnterController(
        createGameUsecase = CreateGameUsecase(gameRepository, playerRepository)
    )
    private val gameController = GameController(
        loadGameUsecase = LoadGameUsecase(gameRepository),
        getAvailableActionUsecase = GetAvailableActionUsecase(),
        sendActionUsecase = SendActionUsecase(randomValueGenerator)
    )

    fun run() {
        enterController.run()

        val gameId = enterController.gameId()

        gameController.run(gameId = gameId)
    }
}