package com.udistrital.minigolf.model

/**
 * Representa la pelota en el campo.
 * Solo contiene datos de posición y tamaño, sin lógica.
 */
data class Ball(
    var x: Float,
    var y: Float,
    val radius: Float,
    var vx: Float = 0f,
    var vy: Float = 0f
) {
    fun update(friction: Float) {
        x += vx
        y += vy
        vx *= friction
        vy *= friction
    }

    fun stop() {
        vx = 0f
        vy = 0f
    }
}