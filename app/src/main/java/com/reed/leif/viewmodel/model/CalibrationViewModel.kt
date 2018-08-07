package com.reed.leif.viewmodel.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.graphics.PointF
import android.net.Uri
import com.reed.leif.data.repository.SessionRepository
import java.io.File

class CalibrationViewModel(
        private val sessionRepository: SessionRepository,
        private val sessionId: Long
): ViewModel() {

    val position: LiveData<PointF>
    val imageUri: LiveData<Uri>

    init {
        position = sessionRepository.getCalibrationPoint(sessionId)
        val imagePath = sessionRepository.getImagePath(sessionId)
        imageUri = Transformations.map(imagePath) {
            Uri.fromFile(File(it))
        }
    }

    fun setCalibrationPosition(p: PointF) = sessionRepository.setCalibrationPoint(sessionId, p)
}