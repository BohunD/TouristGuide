package com.db.apps.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "place_id") val place_id: String,
    @ColumnInfo(name = "place_name") val place_name: String,
    @ColumnInfo(name = "rating") val rating: Double,
    @ColumnInfo(name = "formatted_address") val formattedAddress: String,
    @ColumnInfo(name = "place_lat") val placeLat: Double,
    @ColumnInfo(name = "place_lng") val placeLng: Double,
    @ColumnInfo(name = "photos") val photos: String?,
    @ColumnInfo(name = "is_liked") var isLiked: Boolean = false
) : Parcelable
