package com.db.apps.presentation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.db.apps.CheckerLD
import com.db.apps.SingleLiveData
import com.db.apps.domain.repository.LocationRepository
import com.db.apps.domain.usecases.GetPlacesNearbyUseCase
import com.db.apps.model.Result
import kotlinx.coroutines.launch

class MapsFragmentViewModel(
    private val activity: Activity,
    private val getPlacesNearbyUseCase: GetPlacesNearbyUseCase
) : ViewModel() {
     val placesNearbyLiveData = SingleLiveData<List<Result>>()

    suspend fun getPlacesNearby(lat: Double, lng: Double, placeTypes: List<String>) {
        viewModelScope.launch {
            try {
                val placesNearby = getPlacesNearbyUseCase.execute(lat, lng, placeTypes)
                placesNearbyLiveData.value = placesNearby
                Log.d("MyLog2", placeTypes.toString())
            } catch (e: Exception) {
                // Обработка ошибок, например, показ сообщения об ошибке пользователю
            }
        }
    }
}