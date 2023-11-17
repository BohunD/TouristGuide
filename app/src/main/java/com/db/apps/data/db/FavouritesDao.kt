package com.db.apps.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.db.apps.model.PlaceEntity

@Dao
interface FavouritesDao {
    @Query("SELECT * FROM places")
    fun getAll(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE place_id IN (:placeId)")
    fun loadAllByIds(placeId: String): List<PlaceEntity>

    @Insert
    fun insert(vararg place: PlaceEntity)

    @Delete
    fun delete(place: PlaceEntity)
}