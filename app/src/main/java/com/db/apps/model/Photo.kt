package com.db.apps.model

data class Photo(
    val height: Int?=null,
    val html_attributions: List<String>?=null,
    val photo_reference: String?=null,
    val width: Int?=null
)