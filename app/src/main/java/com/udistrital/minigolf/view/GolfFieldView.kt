package com.udistrital.minigolf.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.udistrital.minigolf.model.Ball
import com.udistrital.minigolf.model.Hole
import androidx.core.graphics.toColorInt

class GolfFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val fieldPaint = Paint().apply { color = "#2E7D32".toColorInt() } // verde césped
    private val holePaint = Paint().apply { color = Color.BLACK }
    private val ballPaint = Paint().apply { color = Color.WHITE }
    private val guidePaint = Paint().apply {
        color = Color.WHITE
        alpha = 150
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private var ball: Ball? = null
    private var hole: Hole? = null
    private var tiltX: Float = 0f
    private var tiltY: Float = 0f

    /**
     * Único punto de entrada para actualizar qué se dibuja.
     * MainActivity llama a esto cuando el estado cambia.
     */
    fun setElements(ball: Ball, hole: Hole) {
        this.ball = ball
        this.hole = hole
        invalidate()
    }

    fun setTilt(x: Float, y: Float) {
        this.tiltX = x
        this.tiltY = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Fondo del campo
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fieldPaint)

        hole?.let {
            canvas.drawCircle(it.x, it.y, it.radius, holePaint)
        }

        ball?.let { b ->
            // Dibujar línea de guía si la pelota está quieta
            if (Math.abs(b.vx) < 0.1f && Math.abs(b.vy) < 0.1f) {
                // Multiplicamos por un factor para que la línea sea visible (ej. 50)
                canvas.drawLine(
                    b.x, b.y,
                    b.x + tiltX * 50f, b.y + tiltY * 50f,
                    guidePaint
                )
            }
            canvas.drawCircle(b.x, b.y, b.radius, ballPaint)
        }
    }
}