package com.example.budgetbuddy.ui.main.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(val name: String, val value: Float, val color: Int)

    var slices: List<Slice> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 36f
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        if (total == 0f) return

        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(cx, cy) - 20f

        var startAngle = -90f
        for (slice in slices) {
            val sweep = (slice.value / total) * 360f
            paint.color = slice.color
            canvas.drawArc(
                cx - radius, cy - radius, cx + radius, cy + radius,
                startAngle, sweep, true, paint
            )
            startAngle += sweep
        }

        // Draw center text (total)
        canvas.drawText("R %.0f".format(total), cx, cy + 12f, textPaint)
    }
}