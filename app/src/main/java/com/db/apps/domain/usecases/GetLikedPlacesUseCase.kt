package com.db.apps.domain.usecases

import androidx.lifecycle.LiveData
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceEntity

class GetLikedPlacesUseCase(
    private val repository: LocationRepository
) {
    suspend fun execute(): LiveData<List<PlaceEntity>>{
        return repository.getLikedPlaces()
    }
}