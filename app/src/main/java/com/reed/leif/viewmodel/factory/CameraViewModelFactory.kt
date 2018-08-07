package com.reed.leif.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.reed.leif.data.repository.SessionRepository
import com.reed.leif.viewmodel.model.CameraViewModel

class CameraViewModelFactory(
        private val sessionRepository: SessionRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun<T: ViewModel> create(modelClass: Class<T>):T {
        return CameraViewModel(sessionRepository) as T
    }
}