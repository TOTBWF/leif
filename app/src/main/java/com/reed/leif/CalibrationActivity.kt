package com.reed.leif

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.PointF
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import com.reed.leif.model.NavigationDBHelper
import kotlinx.android.synthetic.main.activity_calibration.*
import java.io.File

class CalibrationActivity : AppCompatActivity() {

    private var sessionId : Long = -1
    private lateinit var dbHelper : NavigationDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        mapView.setOnTouchListener(::onTouchMap)
        sessionId = intent.getLongExtra(SESSION_ID, -1)
        dbHelper = NavigationDBHelper(this)
        val photoUri = Uri.fromFile(File(dbHelper.getPhotoPath(sessionId)))
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        mapView.setImageBitmap(bitmap)
        calibrationButton.setOnClickListener(::completeCalibration)
        dot.position = PointF(mapView.width/2f, mapView.height/2f)
    }

    private fun onTouchMap(view : View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> dot.position = PointF(event.x, event.y)
        }
        return true
    }

    fun completeCalibration(view: View?) {
        val intent = Intent(this, NavigationActivity::class.java)
        dbHelper.setCalibrationPosition(sessionId, dot.position)
        intent.putExtra(SESSION_ID, sessionId)
        startActivity(intent)
    }
}
