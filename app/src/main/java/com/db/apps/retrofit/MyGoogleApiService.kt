package com.db.apps.retrofit

import com.db.apps.model.RootPhotos
import com.db.apps.model.Root
import com.db.apps.model.RootAttraction
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface MyGoogleApiService {
    @GET
    fun getPlacesNearby(@Url url:String):Call<Root>

    @GET
    fun getPlacesOfInterest(@Url url:String): Call<RootAttraction>
    @GET
    fun getPhotos(@Url url:String): Call<RootPhotos>
}