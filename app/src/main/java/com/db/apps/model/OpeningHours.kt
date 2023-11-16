package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpeningHours(
    val open_now: Boolean?=null
) : Parcelable