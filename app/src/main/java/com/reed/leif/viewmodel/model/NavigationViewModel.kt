package com.reed.leif.viewmodel.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.graphics.PointF
import com.reed.leif.data.entity.Location
import com.reed.leif.data.repository.LocationRepository
import com.reed.leif.util.Projection

class NavigationViewModel(
        private val locationRepository: LocationRepository,
        private val sessionId : Long
) : ViewModel() {

    val locations: LiveData<List<Location>> = locationRepository.getLocations(sessionId)
    val points: LiveData<List<PointF>>

    init {
        points = Transformations.map(locations) {ls ->
            val origin = ls.firstOrNull()?.let { Projection.mercator(it) }
            ls.map { l ->
                val p = Projection.mercator(l)
                PointF(p.x - (origin?.x ?: 0f), p.y - (origin?.y ?: 0f))
            }
        }
    }

    fun addLocation(l: android.location.Location) = locationRepository.addLocation(sessionId, l)
}