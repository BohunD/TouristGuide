package com.db.apps.domain.usecases

import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceEntity
import com.db.apps.model.ResultAttraction

class AddToFavouritesUseCase(private val repository: LocationRepository) {
    suspend fun execute(place: ResultAttraction){
        repository.addPlaceToFavourites(place)
    }

    suspend fun execute(place: PlaceEntity){
        repository.addPlaceToFavourites(place)
    }
}