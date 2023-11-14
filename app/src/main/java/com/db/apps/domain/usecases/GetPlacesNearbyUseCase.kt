package com.db.apps.domain.usecases

import android.location.Location
import android.util.Log
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceInfo
import com.db.apps.model.Result

class GetPlacesNearbyUseCase(private val repository: LocationRepository) {
    suspend fun execute(lat: Double, lng: Double, placeTypes: List<String>): List<Result>{
        Log.d("MyLog4", repository.getPlacesNearby(lat, lng, placeTypes).toString())
        return repository.getPlacesNearby(lat, lng, placeTypes)
    }
}