package com.db.apps.model

import com.google.gson.annotations.SerializedName

data class ResultPhotos(
    @SerializedName("photos"                ) var photos              : ArrayList<Photo> = arrayListOf()
)
