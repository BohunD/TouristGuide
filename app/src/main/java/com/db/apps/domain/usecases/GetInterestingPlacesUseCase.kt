package com.db.apps.domain.usecases

import com.db.apps.model.Location
import com.db.apps.model.PlaceInfo
import com.db.apps.model.PlaceItemRv

interface GetInterestingPlacesUseCase {
    fun execute(location: Location): List<PlaceInfo>
}