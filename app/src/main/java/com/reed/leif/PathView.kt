package com.reed.leif

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.location.Location
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.*

class PathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var center : PointF = PointF(0f,0f)
        set(p) {
            field = p
            invalidate()
        }
    private var origin : PointF? = null
    private val locations = mutableListOf<Location>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokeWidth = 10f
    private val scale = Math.pow(2.0, 17.0).toFloat()
    private val transformation = Matrix()

    private lateinit var previousPoint : PointF

    private var toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)

    init {
        paint.color = ContextCompat.getColor(context!!, R.color.red)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
//        transformation.pre(2f, 2f)
        this.setOnTouchListener(::onTouch)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val linePoints = pointsToLines(locations.map(::mercator))
        transformation.mapPoints(linePoints)
        canvas?.drawLines(linePoints, paint)
    }

    fun pointsToLines(points: List<PointF>): FloatArray {
        return when (points.size) {
            0 -> floatArrayOf()
            1 -> {
                val p = points.first()
                floatArrayOf(p.x, p.y, p.x, p.y)
            }
            2 -> {
                val p0 = points.first()
                val p1 = points.last()
                floatArrayOf(p0.x, p0.y, p1.x, p1.y)
            }
            else -> {
                val linePoints = FloatArray((points.size - 1)*4)
                val fst = points.first()
                val last = points.last()
                linePoints[0] = fst.x
                linePoints[1] = fst.y
                linePoints[linePoints.size - 2] = last.x
                linePoints[linePoints.size - 1] = last.y
                points.drop(1).dropLast(1).forEachIndexed{ i, p ->
                    linePoints[i*4 + 2]  = p.x
                    linePoints[i*4 + 3]  = p.y
                    linePoints[i*4 + 4]  = p.x
                    linePoints[i*4 + 5]  = p.y
                }
                linePoints
            }
        }
    }

    private fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        motionEvent?.let { event ->
            val relativePoint = PointF(event.x - center.x, event.y - center.y)
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val delta = PointF(relativePoint.x - previousPoint.x, relativePoint.y - previousPoint.y)
                    val deltaM = norm(delta)
                    val touchM = norm(relativePoint)
                    val prevTouchM = norm(previousPoint)
                    val scale = touchM / prevTouchM
                    // TODO: The rotation is off by a factor of 2
                    val rotate = atan((deltaM/prevTouchM)*(180/PI))
                    transformation.postScale(scale, scale, center.x, center.y)
                    if(relativePoint.y/relativePoint.x - previousPoint.y/previousPoint.x >= 0) {
                        transformation.postRotate(rotate.toFloat(), center.x, center.y)
                    } else {
                        transformation.postRotate(-rotate.toFloat(), center.x, center.y)
                    }
                    invalidate()
                }
            }
            previousPoint = relativePoint
        }
        return true
    }

    private fun norm(v : PointF) : Float {
        return sqrt(v.x*v.x + v.y*v.y)
    }

    private fun mercator(l: Location): PointF {
        // Scales the values to a 256x256 grid
        val scaleFactor = 256/(2*PI)
        // Perform mercator transformation
        val x = scaleFactor*(Math.toRadians(l.longitude) + PI)
        val y = scaleFactor*(PI - ln(tan(PI/4 + Math.toRadians(l.latitude)/2)))
        // Center with respect to the origin
        val scaledX = x.toFloat() - (origin?.x ?: 0f)
        val scaledY = y.toFloat() - (origin?.y ?: 0f)
        return PointF(scaledX, scaledY)
    }

    fun addLocation(l : Location) {
        if(origin == null) {
            // Ensure that our coordinate system starts a the origin
            val p = mercator(l)
            origin = PointF(p.x, p.y)
            // Ensure that the origin gets translated to the calibration center
            transformation.postTranslate(center.x, center.y)
            transformation.postScale(scale, scale, center.x, center.y)
        }
        val p = mercator(l)
        val tp = floatArrayOf(p.x, p.y)
        transformation.mapPoints(tp)
        Log.d("[Leif]", "Lat: " + l.latitude + " Lon: " + l.longitude)
        Log.d("[Leif]", "original x: " + p.x + " y: " + p.y)
        Log.d("[Leif]", "transformed x: " + tp[0] + " y: " + tp[1])
        locations.add(l)
        invalidate()
    }
}