package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Northeast(
    val lat: Double?=null,
    val lng: Double?=null
) : Parcelable