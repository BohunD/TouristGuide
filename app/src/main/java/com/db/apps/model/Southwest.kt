package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Southwest(
    val lat: Double?=null,
    val lng: Double?=null
) : Parcelable
