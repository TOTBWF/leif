package com.reed.leif

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageButton
import com.reed.leif.util.InjectorUtils
import com.reed.leif.util.REQUEST_LOCATION_ACCESS
import com.reed.leif.util.SESSION_ID
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    private var sessionId : Long = -1

    private var locationService : LocationService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            locationService?.requestLocationUpdates(sessionId)
        }

    }

    private var locationEnabled = true
    private var locked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        sessionId = intent.getLongExtra(SESSION_ID, -1)
        Log.d(com.reed.leif.util.TAG, "Navigating session $sessionId")

        val calibrationModel = InjectorUtils.getCalibrationViewModel(this, sessionId)
        val navigationModel = InjectorUtils.getNavigationViewModel(this, sessionId)

        calibrationModel.imageUri.observe(this, Observer { uri ->
            mapView.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver, uri))
        })

        calibrationModel.position.observe(this, Observer { pos ->
            pathView.center = pos ?: PointF(0f, 0f)
        })

        navigationModel.points.observe(this, Observer { ps ->
            pathView.points = ps ?: listOf()
        })

        navigationModel.locations.observe(this, Observer { ls ->
            locationDisplayView.text = ls?.lastOrNull()?.let {
                "Lat: ${it.latitude} Lon: ${it.longitude}"
            } ?: "No Location"
        })

        pauseButton.setOnClickListener { view ->
            val button = view as ImageButton
            if(locationEnabled) {
                button.setImageResource(R.drawable.ic_play_circle_outline_white_48dp)
                locationService?.removeLocationUpdates()
            } else {
                button.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp)
                locationService?.requestLocationUpdates(sessionId)
            }
            locationEnabled = !locationEnabled
        }

        lockButton.setOnClickListener { view ->
            val button = view as ImageButton
            if(pathView.locked) {
                button.setImageResource(R.drawable.ic_lock_outline_white_48dp)
            } else {
                button.setImageResource(R.drawable.ic_lock_open_white_48dp)
            }
            pathView.locked = !pathView.locked
        }

        checkPermissions()
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_ACCESS)
        }
    }

}

