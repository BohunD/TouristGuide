package com.db.apps.domain.repository

import com.db.apps.model.PlaceEntity
import com.db.apps.model.Result
import com.db.apps.model.ResultAttraction

interface LocationRepository {
    suspend fun getPlacesNearby(lat: Double, lng: Double, placeTypes: List<String>): List<Result>

    suspend fun addPlaceToFavourites(place: ResultAttraction)
    suspend fun addPlaceToFavourites(place: PlaceEntity)

    suspend fun getPlacesFromFavourites(): List<PlaceEntity>
}