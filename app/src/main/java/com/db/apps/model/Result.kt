package com.db.apps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Result(
    val business_status: String?=null,
    val geometry: Geometry?=null,
    val icon: String?=null,
    val icon_background_color: String?=null,
    val icon_mask_base_uri: String?=null,
    val name: String?=null,
    val opening_hours: OpeningHours?=null,
    val photos: List<Photo>?=null,
    val place_id: String?=null,
    val price_level: Int?=null,
    val rating: Double?=null,
    val reference: String?=null,
    val scope: String?=null,
    val types: List<String>?=null,
    val user_ratings_total: Int?=null,
    val vicinity: String?=null
) : Parcelable
