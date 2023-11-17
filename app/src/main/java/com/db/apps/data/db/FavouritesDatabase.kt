package com.db.apps.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.db.apps.model.PlaceEntity

@Database(entities = [PlaceEntity::class], version = 1)
abstract class FavouritesDatabase: RoomDatabase() {
    abstract fun favouritesDao(): FavouritesDao
}