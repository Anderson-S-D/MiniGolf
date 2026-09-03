package com.udistrital.minigolf.model

/**
 * Representa el hoyo. Es inmutable porque su posición
 * no cambia durante la partida de un mismo nivel.
 */
data class Hole(
    val x: Float,
    val y: Float,
    val radius: Float
)