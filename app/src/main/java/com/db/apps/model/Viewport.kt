package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Viewport(
    val northeast: Northeast?=null,
    val southwest: Southwest?=null
) : Parcelable