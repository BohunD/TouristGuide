package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geometry(
    val location: Location?=null,
    val viewport: Viewport?=null
) : Parcelable