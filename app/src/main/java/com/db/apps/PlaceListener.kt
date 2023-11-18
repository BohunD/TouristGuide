package com.db.apps
import com.db.apps.model.PlaceEntity
import com.db.apps.model.ResultAttraction

interface PlaceListener {
    /*fun onLayoutClick(place: ResultAttraction)*/
    suspend fun onLikeClick(place: ResultAttraction)
    fun onLayoutClick(place: PlaceEntity)
    suspend fun onLikeClick(place: PlaceEntity)

}