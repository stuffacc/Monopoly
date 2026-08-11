package org.example.project

import org.example.project.data.RandomValueGenerator
import org.example.project.data.repository.GameRepositoryImpl
import org.example.project.data.repository.PlayerRepositoryImpl


fun main() {
    val playerRepository = PlayerRepositoryImpl()
    val gameRepository = GameRepositoryImpl()
    val randomValueGenerator = RandomValueGenerator()

    val applicationController = ApplicationController(playerRepository, gameRepository, randomValueGenerator)

    applicationController.run()
}