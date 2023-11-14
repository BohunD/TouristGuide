package com.db.apps.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.db.apps.data.repository.LocationRepositoryImpl
import com.db.apps.domain.usecases.GetPlacesNearbyUseCase

class MapsFragmentViewModelFactory(private val activity: Activity): ViewModelProvider.Factory {

    private val repository by lazy {
        LocationRepositoryImpl()
    }

    private val getPlacesNearbyUseCase by lazy {
        GetPlacesNearbyUseCase(repository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapsFragmentViewModel::class.java)){
            return MapsFragmentViewModel(activity, getPlacesNearbyUseCase) as T
        }
        else throw Exception("Unknown class")
    }
}