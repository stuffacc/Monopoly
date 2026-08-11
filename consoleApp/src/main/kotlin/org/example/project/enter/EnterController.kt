package org.example.project.enter

import org.example.project.domain.models.player.SendPlayer
import org.example.project.domain.usecase.CreateGameUsecase

class EnterController(
    private val createGameUsecase: CreateGameUsecase
) {
    private val sendPlayers: MutableList<SendPlayer> = mutableListOf()
    private var gameId: String? = null

    fun run() {
        println("В игре \"Монополия\" участвует от 2 до 6 игроков.")
        println("Со следующей строки вводите имена игроков, которые будут использоваться в игре. Если вы ввели имена всех игроков - нажмите \"Enter\", начнётся игра")

        while (sendPlayers.size < 6) {
            print("Введите имя игрока под номером ${sendPlayers.size + 1}: ")
            val playerName = readln()

            if (playerName.isEmpty()) {
                if (sendPlayers.size >= 2) {
                    break
                }

                println("В игре \"Монополия\" участвует от 2 до 6 игроков. Сейчас записано ${sendPlayers.size}")
                continue
            }

            addPlayer(playerName)
        }

        createGame()
    }

    private fun addPlayer(playerName: String) {
        sendPlayers.add(
            SendPlayer(
                name = playerName
            )
        )
    }

    private fun createGame() {
        gameId = createGameUsecase.execute(sendPlayers = sendPlayers)
    }

    fun gameId(): String = gameId.toString()


}