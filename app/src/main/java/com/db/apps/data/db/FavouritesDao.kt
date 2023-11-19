package com.db.apps.data.db

import androidx.lifecycle.LiveData
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
    fun getPlaceById(placeId: String): PlaceEntity

    @Query("SELECT * FROM places WHERE place_id IN (:placeId)")
    fun loadAllByIds(placeId: String): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE is_liked = 1")
    fun getLikedPlaces(): LiveData<List<PlaceEntity>>

    @Query("UPDATE places SET is_liked = :isLiked WHERE place_id = :placeId")
    fun updateIsLiked(placeId: String, isLiked: Boolean)

    @Insert
    fun insert(vararg place: PlaceEntity)

    @Delete
    fun delete(place: PlaceEntity)
}