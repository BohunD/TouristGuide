package com.db.apps.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val lat: Double?=null,
    val lng: Double?=null
) : Parcelable