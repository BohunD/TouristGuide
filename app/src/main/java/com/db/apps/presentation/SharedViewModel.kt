package com.db.apps.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.db.apps.CheckerLD
import com.db.apps.SingleLiveData
import com.google.android.gms.maps.model.LatLng

class SharedViewModel: ViewModel() {

    val latLngLD= SingleLiveData<LatLng>()
    private val _locationReceivedLD= SingleLiveData<CheckerLD<Boolean>>()
    val locationReceivedLD: SingleLiveData<CheckerLD<Boolean>>
        get() = _locationReceivedLD

    fun saveLatLng(data: LatLng){
        latLngLD.value = data
    }

    fun setLocationReceived(flag: Boolean){
        _locationReceivedLD.value = CheckerLD(flag)
    }
}