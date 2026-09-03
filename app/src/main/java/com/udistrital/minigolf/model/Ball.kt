package com.udistrital.minigolf.model

/**
 * Representa la pelota en el campo.
 * Solo contiene datos de posición y tamaño, sin lógica.
 */
data class Ball(
    var x: Float,
    var y: Float,
    val radius: Float
)