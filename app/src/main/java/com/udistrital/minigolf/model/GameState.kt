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

    var isGameOver: Boolean = false
        private set

    fun incrementStroke() {
        strokes++
    }

    fun checkHole(ball: Ball, hole: Hole): Boolean {
        val dx = ball.x - hole.x
        val dy = ball.y - hole.y
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
        
        // Si la distancia es menor al radio del hoyo y la pelota va a una velocidad razonable
        if (distance < hole.radius && Math.abs(ball.vx) < 7f && Math.abs(ball.vy) < 7f) {
            isGameOver = true
            return true
        }
        return false
    }

    fun reset(ball: Ball) {
        strokes = 0
        isGameOver = false
        ball.x = initialBallPosition.first
        ball.y = initialBallPosition.second
        ball.stop()
    }
}