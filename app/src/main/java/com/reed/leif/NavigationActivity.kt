package com.reed.leif

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_navigation.*
import java.io.File

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val photoUri = Uri.fromFile(File(intent.getStringExtra(PHOTO_PATH)))
        mapView.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver, photoUri))
        setupLocationListener()
        pathView.center = intent.getParcelableExtra(CALIBRATION_LOCATION)
    }

    private fun setupLocationListener() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_ACCESS)
        } else {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(l: Location?) {
                    l?.let {pathView.addLocation(it)}
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                }

                override fun onProviderEnabled(p0: String?) {
                }

                override fun onProviderDisabled(p0: String?) {
                }
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        }

    }
}

