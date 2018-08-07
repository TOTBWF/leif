package com.reed.leif.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.support.v4.content.ContextCompat
import android.view.View
import android.util.AttributeSet
import com.reed.leif.R

/**
 * Used to display the dot overlay during the calibration screen
* */
class DotView(context: Context?, attrs : AttributeSet) : View(context, attrs) {

    private val radius : Float = 25f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var position : PointF = PointF(0f,0f)
        set(point) {
            field = point
            invalidate()
        }

    init {
//        // If we can, center the point on the screen

        // Update the paint color
        paint.color = ContextCompat.getColor(context!!, R.color.red)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawCircle(position.x, position.y, radius, paint)
    }
}