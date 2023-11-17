package com.db.apps.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
data class Geometry(
    val location: Location?=null,
    val viewport: Viewport?=null
) : Parcelable