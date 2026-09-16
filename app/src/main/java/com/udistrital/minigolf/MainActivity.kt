package com.udistrital.minigolf

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.udistrital.minigolf.model.Ball
import com.udistrital.minigolf.model.GameState
import com.udistrital.minigolf.model.Hole
import com.udistrital.minigolf.util.ScoreStore
import com.udistrital.minigolf.view.GolfFieldView
import kotlin.math.sqrt

class MainActivity : Activity(), SensorEventListener {

    private lateinit var golfFieldView: GolfFieldView
    private lateinit var tvHoleInfo: TextView
    private lateinit var tvStrokes: TextView
    private lateinit var btnReset: Button
    private lateinit var btnBack: ImageButton

    private lateinit var forceBar: ProgressBar
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null


    // Posiciones definidas de forma simple por ahora.
    // Más adelante esto podría venir de una configuración de nivel.
    private val ball = Ball(x = 300f, y = 1200f, radius = 25f)
    private val hole = Hole(x = 300f, y = 400f, radius = 60f)
    private val gameState = GameState(
        holeNumber = 1,
        par = 3,
        initialBallPosition = Pair(ball.x, ball.y)
    )

    // Valores de inclinación actuales
    private var tiltX = 0f
    private var tiltY = 0f

    // Para el bucle de física
    private val handler = Handler(Looper.getMainLooper())
    private val physicsRunnable = object : Runnable {
        override fun run() {
            updatePhysics()
            handler.postDelayed(this, 16) // ~60 FPS
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        forceBar = findViewById(R.id.forceBar)
        // Evita que el HUD (golpes, hoyo, botón volver) o el botón de
        // reinicio queden ocultos detrás de la barra de estado o de
        // navegación (edge-to-edge en Android 15+). Sin esto, en pantallas
        // donde el sistema dibuja encima del contenido se ve solo una
        // parte de la interfaz.
        val root = findViewById<View>(R.id.gameRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        golfFieldView = findViewById(R.id.golfFieldView)
        tvHoleInfo = findViewById(R.id.tvHoleInfo)
        tvStrokes = findViewById(R.id.tvStrokes)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        btnReset.setOnClickListener { onResetClicked() }
        btnBack.setOnClickListener { finish() }

        renderInitialState()
        handler.post(physicsRunnable)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            tiltX = -event.values[0]
            tiltY = event.values[1]

            // Enviamos la inclinación a la vista
            golfFieldView.setTilt(tiltX, tiltY)

            // --- NUEVO: MEDIDOR DE POTENCIA EN VIVO ---
            // Solo actualizamos la barra si la pelota está quieta (estás apuntando)
            if (Math.abs(ball.vx) < 0.1f && Math.abs(ball.vy) < 0.1f) {
                // Calculamos la fuerza potencial basada en la inclinación
                val potentialForce = Math.hypot(tiltX.toDouble() * 2.0, tiltY.toDouble() * 2.0).toFloat()
                val maxForce = 25f
                val progress = ((potentialForce / maxForce) * 100).toInt()

                forceBar.progress = progress.coerceIn(0, 100)
            }

            // Detectar un "golpe" brusco
            val totalAcceleration = sqrt(event.values[0] * event.values[0] +
                    event.values[1] * event.values[1] +
                    event.values[2] * event.values[2])

            if (totalAcceleration > 15f && Math.abs(ball.vx) < 0.1f && Math.abs(ball.vy) < 0.1f) {
                shootBall()
            }
        }
    }

    private fun shootBall() {
        if (gameState.isGameOver) return

        // La fuerza del disparo actual
        ball.vx = tiltX * 2f
        ball.vy = tiltY * 2f

        // 1. Calcular la magnitud de la fuerza usando la hipotenusa
        val forceMagnitude = Math.hypot(ball.vx.toDouble(), ball.vy.toDouble()).toFloat()

        // 2. Definir una fuerza máxima esperada para calcular el porcentaje
        // Un valor de 25f es un buen límite superior basado en la gravedad
        val maxForce = 25f

        val progress = ((forceMagnitude / maxForce) * 100).toInt()

        forceBar.progress = progress.coerceIn(0, 100)

        gameState.incrementStroke()
        updateHud()
    }

    private fun updatePhysics() {
        if (gameState.isGameOver) return

        ball.update(friction = 0.98f)

        // Colisiones con bordes (basado en el tamaño de la vista)
        if (Math.abs(ball.vx) < 0.1f && Math.abs(ball.vy) < 0.1f) {
            forceBar.progress = 0
        }
        if (ball.x - ball.radius < 0 || ball.x + ball.radius > golfFieldView.width) {
            ball.vx *= -0.5f // Rebote con pérdida de energía
            ball.x = if (ball.x - ball.radius < 0) ball.radius else golfFieldView.width - ball.radius
        }
        if (ball.y - ball.radius < 0 || ball.y + ball.radius > golfFieldView.height) {
            ball.vy *= -0.5f
            ball.y = if (ball.y - ball.radius < 0) ball.radius else golfFieldView.height - ball.radius
        }

        // Verificar si entró al hoyo
        if (gameState.checkHole(ball, hole)) {
            ball.stop()
            ScoreStore.saveIfBetter(this, gameState.strokes)
            val message = "¡Felicitaciones! Metiste la pelota en ${gameState.strokes} golpes"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            // Reiniciar automáticamente después de 3 segundos
            handler.postDelayed({
                onResetClicked()
            }, 3000)
        }

        golfFieldView.invalidate()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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
