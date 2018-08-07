package com.reed.leif.viewmodel.model

import android.arch.lifecycle.ViewModel
import com.reed.leif.data.repository.SessionRepository

class CameraViewModel(private val sessionRepository: SessionRepository) : ViewModel() {

    fun createSession(imagePath: String) = sessionRepository.createSession(imagePath)
}