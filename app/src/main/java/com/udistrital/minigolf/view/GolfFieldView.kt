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

    private var ball: Ball? = null
    private var hole: Hole? = null

    /**
     * Único punto de entrada para actualizar qué se dibuja.
     * MainActivity llama a esto cuando el estado cambia.
     */
    fun setElements(ball: Ball, hole: Hole) {
        this.ball = ball
        this.hole = hole
        invalidate() // solicita un redibujado
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Fondo del campo
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fieldPaint)

        hole?.let {
            canvas.drawCircle(it.x, it.y, it.radius, holePaint)
        }

        ball?.let {
            canvas.drawCircle(it.x, it.y, it.radius, ballPaint)
        }
    }
}