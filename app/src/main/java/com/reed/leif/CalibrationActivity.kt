package com.reed.leif

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import com.reed.leif.util.InjectorUtils
import com.reed.leif.util.SESSION_ID
import kotlinx.android.synthetic.main.activity_calibration.*

class CalibrationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        val sessionId = intent.getLongExtra(SESSION_ID, -1)
        Log.d(com.reed.leif.util.TAG, "Calibrating session $sessionId")

        val viewModel = InjectorUtils.getCalibrationViewModel(this, sessionId)

        viewModel.imageUri.observe(this, Observer { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            mapView.setImageBitmap(bitmap)
        })

        mapView.setOnTouchListener{ _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val p = PointF(event.x, event.y)
                    viewModel.setCalibrationPosition(p)
                    dot.position = p
                }
            }
            true
        }

        calibrationButton.setOnClickListener{
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra(SESSION_ID, sessionId)
            startActivity(intent)
        }
    }
}
