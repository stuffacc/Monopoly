package org.example.project

import org.example.project.data.RandomValueGeneratorImpl
import org.example.project.data.repository.GameRepositoryImpl
import org.example.project.data.repository.PlayerRepositoryImpl


fun main() {
    val playerRepository = PlayerRepositoryImpl()
    val gameRepository = GameRepositoryImpl()
    val randomValueGenerator = RandomValueGeneratorImpl()

    val applicationController = ApplicationController(playerRepository, gameRepository, randomValueGenerator)

    applicationController.run()
}