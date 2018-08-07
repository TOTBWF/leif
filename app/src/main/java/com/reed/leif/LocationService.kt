package com.reed.leif

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.reed.leif.util.InjectorUtils
import com.reed.leif.util.LOCATION_NOTIFICATION_CHANNEL_ID
import com.reed.leif.util.LOCATION_NOTIFICATION_ID
import com.reed.leif.util.TAG
import com.reed.leif.viewmodel.model.NavigationViewModel

class LocationService : Service() {

    private val binder = LocalBinder()
    private lateinit var locationManager : LocationManager
    private var navigationViewModel: NavigationViewModel? = null
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(l: Location) {
            Log.d("[Leif]", "Lat: " + l.latitude + " Lon: " + l.longitude)
            navigationViewModel?.addLocation(l)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

    override fun onCreate() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)
    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        return binder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // We will only have one activity bound to this service
        startForeground(LOCATION_NOTIFICATION_ID, getNotification())
        return true
    }

    fun requestLocationUpdates(sessionId: Long) {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Starting location service for session $sessionId")
                navigationViewModel = InjectorUtils.getNavigationViewModel(this, sessionId)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
                startService(Intent(applicationContext, LocationService::class.java))
            }
            else -> {
                Log.e(TAG, "Location Permission not granted!")
            }
        }
    }

    fun removeLocationUpdates() {
        locationManager.removeUpdates(locationListener)
        stopSelf()
    }

    private fun getNotification() : Notification {
        val intent = Intent (this, NavigationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
                .setContentText("Recording GPS position")
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
        return builder.build()
    }

    inner class LocalBinder : Binder() {
        fun getService() : LocationService {
            return this@LocationService
        }
    }
}
