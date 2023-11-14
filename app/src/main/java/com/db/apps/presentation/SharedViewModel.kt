package com.db.apps.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.db.apps.CheckerLD
import com.google.android.gms.maps.model.LatLng

class SharedViewModel: ViewModel() {

    val latLngLD= MutableLiveData<LatLng>()
    private val _locationReceivedLD= MutableLiveData<CheckerLD<Boolean>>()
    val locationReceivedLD: LiveData<CheckerLD<Boolean>>
        get() = _locationReceivedLD

    fun saveLatLng(data: LatLng){
        latLngLD.value = data
    }

    fun setLocationReceived(flag: Boolean){
        _locationReceivedLD.value = CheckerLD(flag)
    }
}