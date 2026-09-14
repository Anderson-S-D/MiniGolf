package com.udistrital.minigolf

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udistrital.minigolf.util.ScoreStore

/**
 * Pantalla de inicio del juego.
 * Responsabilidad única: mostrar el nombre del juego, el mejor puntaje
 * alcanzado y permitir iniciar una nueva partida. No conoce nada sobre
 * sensores ni sobre la lógica del juego en sí.
 */
class HomeActivity : Activity() {

    private lateinit var tvBestScore: TextView
    private lateinit var btnPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Evita que el contenido quede oculto detrás de la barra de estado
        // o la barra de navegación (edge-to-edge en Android 15+).
        val root = findViewById<View>(R.id.homeRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tvBestScore = findViewById(R.id.tvBestScore)
        btnPlay = findViewById(R.id.btnPlay)

        btnPlay.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Se actualiza cada vez que se vuelve al menú, por si se completó
        // un hoyo con un mejor puntaje durante la partida.
        updateBestScore()
    }

    private fun updateBestScore() {
        val best = ScoreStore.getBestStrokes(this)
        tvBestScore.text = if (best != null) {
            getString(R.string.mejor_puntaje_valor, best)
        } else {
            getString(R.string.mejor_puntaje_vacio)
        }
    }
}
