package com.udistrital.minigolf.util

import android.content.Context

/**
 * Guarda y recupera el mejor puntaje (menor cantidad de golpes con la que
 * se ha completado el hoyo) usando SharedPreferences.
 *
 * Responsabilidad única: persistencia simple del mejor puntaje. No conoce
 * nada sobre sensores, física ni la interfaz del juego.
 */
object ScoreStore {

    private const val PREFS_NAME = "minigolf_prefs"
    private const val KEY_BEST_STROKES = "best_strokes"

    /**
     * Retorna el mejor puntaje guardado, o null si el jugador aún no ha
     * completado ningún hoyo.
     */
    fun getBestStrokes(context: Context): Int? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val value = prefs.getInt(KEY_BEST_STROKES, -1)
        return if (value >= 0) value else null
    }

    /**
     * Guarda el puntaje recibido únicamente si es mejor (menor) que el
     * puntaje ya almacenado, o si todavía no existe un puntaje guardado.
     */
    fun saveIfBetter(context: Context, strokes: Int) {
        val current = getBestStrokes(context)
        if (current == null || strokes < current) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_BEST_STROKES, strokes)
                .apply()
        }
    }
}
