package com.reed.leif.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.reed.leif.data.repository.LocationRepository
import com.reed.leif.viewmodel.model.NavigationViewModel

class NavigationViewModelFactory(
        private val locationRepository: LocationRepository,
        private val sessionId : Long
) : ViewModelProvider.NewInstanceFactory() {

    override fun<T: ViewModel> create(modelClass: Class<T>):T {
        return NavigationViewModel(locationRepository, sessionId) as T
    }
}