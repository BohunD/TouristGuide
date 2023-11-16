package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val height: Int?=null,
    val html_attributions: List<String>?=null,
    val photo_reference: String?=null,
    val width: Int?=null
) : Parcelable