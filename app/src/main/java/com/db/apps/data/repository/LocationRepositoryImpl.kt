package com.db.apps.data.repository

import android.util.Log
import com.db.apps.Utils
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.model.Result
import com.db.apps.model.Root
import com.db.apps.retrofit.MyGoogleApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl : LocationRepository {

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
