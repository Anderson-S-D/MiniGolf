package com.udistrital.minigolf.model

/**
 * Estado de la partida actual. Responsabilidad única:
 * mantener y mutar el estado del juego (golpes, hoyo, par).
 * No sabe cómo se dibuja ni cómo se detectan los golpes.
 */
class GameState(
    val holeNumber: Int,
    val par: Int,
    private val initialBallPosition: Pair<Float, Float>
) {
    var strokes: Int = 0
        private set

    fun incrementStroke() {
        strokes++
    }

    fun reset(ball: Ball) {
        strokes = 0
        ball.x = initialBallPosition.first
        ball.y = initialBallPosition.second
    }
}