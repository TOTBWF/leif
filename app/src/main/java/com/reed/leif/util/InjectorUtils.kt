package com.reed.leif.util

import android.content.Context
import com.reed.leif.data.AppDatabase
import com.reed.leif.data.repository.LocationRepository
import com.reed.leif.data.repository.SessionRepository
import com.reed.leif.viewmodel.factory.CalibrationViewModelFactory
import com.reed.leif.viewmodel.factory.CameraViewModelFactory
import com.reed.leif.viewmodel.factory.NavigationViewModelFactory
import com.reed.leif.viewmodel.model.CalibrationViewModel
import com.reed.leif.viewmodel.model.CameraViewModel
import com.reed.leif.viewmodel.model.NavigationViewModel

object InjectorUtils {
    private fun getSessionRepository(context: Context): SessionRepository {
        return SessionRepository.getInstance(AppDatabase.getInstance(context).sessionDao())
    }

    private fun getLocationRepository(context: Context) : LocationRepository {
        return LocationRepository.getInstance(AppDatabase.getInstance(context).locationDao())
    }

    fun getCameraViewModel(context: Context): CameraViewModel {
        return CameraViewModelFactory(getSessionRepository(context))
                .create(CameraViewModel::class.java)
    }

    fun getCalibrationViewModel(context: Context, sessionId: Long): CalibrationViewModel {
        return CalibrationViewModelFactory(getSessionRepository(context), sessionId)
                .create(CalibrationViewModel::class.java)
    }

    fun getNavigationViewModel(context: Context, sessionId: Long) : NavigationViewModel {
        return NavigationViewModelFactory(getLocationRepository(context), sessionId)
                .create(NavigationViewModel::class.java)
    }

}