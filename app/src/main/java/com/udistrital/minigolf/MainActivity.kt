package com.udistrital.minigolf

import android.app.Activity
import android.os.Bundle
import com.udistrital.minigolf.model.Ball
import com.udistrital.minigolf.model.GameState
import com.udistrital.minigolf.model.Hole
import com.udistrital.minigolf.view.GolfFieldView
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var golfFieldView: GolfFieldView
    private lateinit var tvHoleInfo: TextView
    private lateinit var tvStrokes: TextView
    private lateinit var btnReset: Button

    // Posiciones definidas de forma simple por ahora.
    // Más adelante esto podría venir de una configuración de nivel.
    private val ball = Ball(x = 300f, y = 1200f, radius = 25f)
    private val hole = Hole(x = 300f, y = 400f, radius = 30f)
    private val gameState = GameState(
        holeNumber = 1,
        par = 3,
        initialBallPosition = Pair(ball.x, ball.y)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        golfFieldView = findViewById(R.id.golfFieldView)
        tvHoleInfo = findViewById(R.id.tvHoleInfo)
        tvStrokes = findViewById(R.id.tvStrokes)
        btnReset = findViewById(R.id.btnReset)

        btnReset.setOnClickListener { onResetClicked() }

        renderInitialState()
    }

    private fun renderInitialState() {
        golfFieldView.setElements(ball, hole)
        updateHud()
    }

    private fun onResetClicked() {
        gameState.reset(ball)
        golfFieldView.setElements(ball, hole)
        updateHud()
    }

    private fun updateHud() {
        tvHoleInfo.text = "Hoyo ${gameState.holeNumber} - Par ${gameState.par}"
        tvStrokes.text = "Golpes: ${gameState.strokes}"
    }
}