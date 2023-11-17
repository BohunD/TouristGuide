package com.db.apps.domain.usecases

import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceEntity

class GetFromFavouritesUseCase(private val repository: LocationRepository) {
    suspend fun execute(): List<PlaceEntity>{
        return repository.getPlacesFromFavourites()
    }
}