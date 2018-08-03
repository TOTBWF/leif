package com.reed.leif

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.reed.leif.model.NavigationDBHelper
import kotlinx.android.synthetic.main.activity_navigation.*
import java.io.File

class NavigationActivity : AppCompatActivity() {

    private val locationReceiver = LocationReceiver()
    private var sessionId : Long = -1
    private lateinit var dbHelper : NavigationDBHelper

    private var locationService : LocationService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        sessionId = intent.getLongExtra(SESSION_ID, -1)
        dbHelper = NavigationDBHelper(this)
        val photoUri = Uri.fromFile(File(dbHelper.getPhotoPath(sessionId)))
        mapView.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver, photoUri))

        pathView.center = dbHelper.getCalibrationPosition(sessionId)
        checkPermissions()
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        locationService?.requestLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(locationReceiver, IntentFilter(LOCATION_UPDATE))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)
        super.onPause()
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

    inner class LocationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val l = intent.getParcelableExtra<Location>(LOCATION_UPDATE_DATA)
            pathView.addLocation(l)
        }
    }

}

