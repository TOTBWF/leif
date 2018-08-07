package com.reed.leif.util

import android.graphics.PointF
import com.reed.leif.data.entity.Location
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan

object Projection {

    fun mercator(l: Location): PointF {
        // Scales the values to a 256x256 grid
        val scaleFactor = 256 / (2 * PI)
        // Perform mercator transformation
        val x = scaleFactor * (Math.toRadians(l.longitude) + PI)
        val y = scaleFactor * (PI - ln(tan(PI / 4 + Math.toRadians(l.latitude) / 2)))
        return PointF(x.toFloat(), y.toFloat())
    }
}