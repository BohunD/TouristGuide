package com.db.apps.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.db.apps.Utils
import com.db.apps.data.db.FavouritesDatabase
import com.db.apps.model.PlaceEntity
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.getPhotoUrl
import com.db.apps.model.Result
import com.db.apps.model.ResultAttraction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl(
    private val context: Context
) : LocationRepository {
    val db = Room.databaseBuilder(
        context,
        FavouritesDatabase::class.java,
        "Favourites"
    ).build()

    override suspend fun getPlacesNearby(
        lat: Double, lng: Double,
        placeTypes: List<String>,
    ): List<Result> {
        val resultList = mutableListOf<Result>()
        val myService = Utils.googleApiService
        for (placeType in placeTypes) {
            val url = buildUrl(lat, lng, placeType)
            try {
                val response = myService.getPlacesNearby(url).await()

                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    resultList.addAll(response.body()!!.results!!)
                    Log.d("MyLog3", "ResultList: $resultList")
                } else {
                    Log.e("LocationRepository", "response is not successful")
                }
            } catch (e: Exception) {
                Log.e("LocationRepository", "Error fetching places nearby", e)
            }
        }
        Log.d("MyLog5", "ResultList: $resultList")
        return resultList
    }

    override suspend fun addPlaceToDb(place: ResultAttraction) {

        val ph= mutableListOf<String>()
        for(photo in place.photos){
            ph.add(getPhotoUrl(photo))
        }
        val entity = PlaceEntity(
            place_id = place.placeId!!,
            place_name = place.name!!,
            rating = place.rating!!,
            formattedAddress = place.formattedAddress!!,
            placeLat = place.geometry?.location!!.lat!!,
            placeLng = place.geometry?.location!!.lng!!,
            photos = ph[0]
        )
        try{
            db.favouritesDao().insert(entity)
            Log.d("RepoImpl, addPlaceToFavourites", "DB NOT contains: $place")
        }catch (e: Exception){
            db.favouritesDao().delete(entity)
            Log.d("RepoImpl, addPlaceToFavourites", "Exception: $e")
        }

    }

    override suspend fun addPlaceToDb(place: PlaceEntity) {

        try{
            db.favouritesDao().insert(place)
            Log.d("RepoImpl, addPlaceToFavourites", "DB NOT contains: $place")
        }catch (e: Exception){
            //db.favouritesDao().delete(place)
            Log.d("RepoImpl, addPlaceToFavourites", "Exception: $e")
        }
    }

    override suspend fun likePlace(place: PlaceEntity) {
        val dbPlace = db.favouritesDao().getPlaceById(placeId = place.place_id)
        if (dbPlace != null) {
            Log.d("GET_BY_ID", dbPlace.toString())
            db.favouritesDao().updateIsLiked(dbPlace.place_id, !dbPlace.isLiked)
        } else {
            Log.e("GET_BY_ID", "Place not found in the database")
        }
    }

    override suspend fun getLikedPlaces(): LiveData<List<PlaceEntity>> {
        return db.favouritesDao().getLikedPlaces()
    }

    override  fun getPlacesFromFavourites(): List<PlaceEntity> {

        Log.d("getPlacesFromFavourites", "${db.favouritesDao().getAll()}")
        return db.favouritesDao().getAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun <T> Call<T>.await(): Response<T> {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    continuation.resume(response) {}
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                try {
                    cancel()
                } catch (ex: Throwable) {
                    // Ignore cancel exception
                }
            }
        }
    }


    private fun buildUrl(lat: Double, lng: Double, placeType: String): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json" )
        googlePlaceUrl.append(
            "?location=$lat,$lng" +
                    "&radius=10000" +
                    "&type=$placeType" +
                    "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw")
        Log.d("My_URL", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }
}