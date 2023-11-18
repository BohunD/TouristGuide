package com.db.apps.presentation.favourites

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.db.apps.data.repository.LocationRepositoryImpl
import com.db.apps.domain.usecases.AddToFavouritesUseCase
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.domain.usecases.GetPlacesNearbyUseCase

@Suppress("UNCHECKED_CAST")
class FavouritesViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {

    private val repository by lazy {
        LocationRepositoryImpl(context)
    }

    private val getFromFavouritesUseCase by lazy {
        GetFromFavouritesUseCase(repository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavouritesViewModel::class.java)){
            return FavouritesViewModel(getFromFavouritesUseCase) as T
        }
        return super.create(modelClass)
    }
}