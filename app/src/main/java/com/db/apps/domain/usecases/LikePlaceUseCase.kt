package com.db.apps.domain.usecases

import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.PlaceEntity

class LikePlaceUseCase(
    private val repository: LocationRepository
) {
    suspend fun execute(place: PlaceEntity){
        repository.likePlace(place)
    }
}