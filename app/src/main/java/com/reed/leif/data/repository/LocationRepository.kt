package com.reed.leif.data.repository

import android.location.Location
import com.reed.leif.data.dao.LocationDao
import com.reed.leif.util.runOnIoThread

class LocationRepository private constructor(private val locationDao: LocationDao) {

    fun getLocations(sessionId: Long) = locationDao.getLocations(sessionId)

    fun addLocation(sessionId: Long, l : Location)  =
        runOnIoThread {
            val loc = com.reed.leif.data.entity.Location(sessionId, l.latitude, l.longitude)
            locationDao.insertLocation(loc)
        }

    companion object {
        @Volatile private var instance: LocationRepository? = null

        fun getInstance(locationDao: LocationDao) =
            instance ?: synchronized(this) {
                instance ?: LocationRepository(locationDao).also { instance = it }
            }
    }
}