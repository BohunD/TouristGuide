package com.db.apps.domain.repository

import android.location.Location
import com.db.apps.model.PlaceInfo
import com.db.apps.model.Result

interface LocationRepository {
    suspend fun getPlacesNearby(lat: Double, lng: Double, placeTypes: List<String>): List<Result>
}