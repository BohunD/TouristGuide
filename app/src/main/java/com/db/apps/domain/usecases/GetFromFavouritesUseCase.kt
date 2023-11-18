package com.db.apps.domain.usecases

import androidx.lifecycle.LiveData
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceEntity

class GetFromFavouritesUseCase(private val repository: LocationRepository) {
    fun execute(): LiveData<List<PlaceEntity>> {
        return repository.getPlacesFromFavourites()
    }
}