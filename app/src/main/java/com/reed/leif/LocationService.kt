package com.reed.leif

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.reed.leif.model.LocationContract
import com.reed.leif.model.LocationContract.LocationEntry
import com.reed.leif.model.NavigationDBHelper
import java.util.*

class LocationService : Service() {

    private val binder = LocalBinder()
    private val broadcastManager = LocalBroadcastManager.getInstance(this)
    private lateinit var locationManager : LocationManager
    private lateinit var dbHelper: NavigationDBHelper
    private var sessionId : Long = -1
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(l: Location) {
            Log.d("[Leif]", "Lat: " + l.latitude + " Lon: " + l.longitude)
            recordLocation(l)
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
        dbHelper = NavigationDBHelper(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        sessionId = intent.getLongExtra(SESSION_ID, -1)
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

    fun requestLocationUpdates() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
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


    private fun recordLocation(l : Location) {
        val intent = Intent(LOCATION_UPDATE)
        intent.putExtra(LOCATION_UPDATE_DATA, l as Parcelable)
        broadcastManager.sendBroadcast(intent)
        dbHelper.insertLocation(sessionId, l)
    }

    inner class LocalBinder : Binder() {
        fun getService() : LocationService {
            return this@LocationService
        }
    }
}
