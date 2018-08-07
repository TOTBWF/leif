package com.reed.leif.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.reed.leif.data.repository.SessionRepository
import com.reed.leif.viewmodel.model.CalibrationViewModel

class CalibrationViewModelFactory(
        private val sessionRepository: SessionRepository,
        private val sessionId: Long
) : ViewModelProvider.NewInstanceFactory() {

    override fun<T: ViewModel> create(modelClass: Class<T>):T {
        return CalibrationViewModel(sessionRepository, sessionId) as T
    }
}
