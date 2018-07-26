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
//        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
//        transformation.pre(2f, 2f)
        this.setOnTouchListener(::onTouch)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val buff = FloatArray(locations.size*2)
//        canvas?.drawCircle(center.x, center.y, 15f, paint)
        locations.map(::mercator).forEachIndexed{ i, p ->
            buff[i*2] = p.x
            buff[i*2 + 1] = p.y
        }
        transformation.mapPoints(buff)
        for(i in buff.indices step 2) {
            canvas?.drawCircle(buff[i], buff[i+1], 10f, paint)
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
                    // Show where (0,0) gets mapped to
//                    val tp = floatArrayOf(0f, 0f)
//                    transformation.mapPoints(tp)
//                    Log.d("[Leif]", "Origin: x: " + tp[0] + " y: " + tp[1])
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
            Log.d("[Leif]", "Point x: " + p.x + " y:" + p.y)
            Log.d("[Leif]", "Center x: " + center.x + " y:" + center.y)
            Log.d("[Leif]", "Origin x: " + origin?.x + " y:" + origin?.y)
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