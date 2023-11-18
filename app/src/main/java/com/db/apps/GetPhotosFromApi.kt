package com.db.apps

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.db.apps.model.Photo
import com.db.apps.model.PlaceEntity
import com.db.apps.model.ResultAttraction
import com.db.apps.model.RootPhotos
import retrofit2.Call
import retrofit2.Response
import java.util.Locale

class GetPhotosFromApi(
    private val place: PlaceEntity
) {
    val photosLD = SingleLiveData<ArrayList<Photo>>()
     fun getPhotos() {
        val myService = Utils.googleApiService
        val url = getPhotosResponse(place)
        val list = arrayListOf<Photo>()
        myService.getPhotos(url).enqueue(object : retrofit2.Callback<RootPhotos> {
            override fun onResponse(call: Call<RootPhotos>, response: Response<RootPhotos>) {
                Log.d("getPhotos", "response: ${response.body()}")
                if (response.isSuccessful && response.body()?.result?.photos?.isNotEmpty()!!) {
                    for (i in 0 until response.body()?.result?.photos?.size!!) {
                        val photo = response.body()?.result?.photos!![i]
                        list.add(photo)
                    }
                    photosLD.value = list
                }
            }

            override fun onFailure(call: Call<RootPhotos>, t: Throwable) {
                Log.d("getPhotos:", "onFailure" + t.message.toString())
            }

        })
         Log.d("getPhotos", "photos: $list")

    }
    private fun getPhotosResponse(place: PlaceEntity): String {
        val placeDetailsUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        placeDetailsUrl.append(
            "?place_id=${place.place_id}" +
                    "&language=${Locale.getDefault()}" +
                    "&fields=photos" +
                    "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw"
        )
        Log.d("My_URL", placeDetailsUrl.toString())
        return placeDetailsUrl.toString()
    }

}