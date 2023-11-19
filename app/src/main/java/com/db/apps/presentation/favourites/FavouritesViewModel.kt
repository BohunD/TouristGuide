package com.db.apps.presentation.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.model.PlaceEntity

class FavouritesViewModel(
): ViewModel() {
    private val _favouritePlacesLD = MutableLiveData<List<PlaceEntity>>()
     val favouritePlacesLD : LiveData<List<PlaceEntity>>
        get() = _favouritePlacesLD

    fun updateList(list: List<PlaceEntity>){
        _favouritePlacesLD.value = list
    }

}