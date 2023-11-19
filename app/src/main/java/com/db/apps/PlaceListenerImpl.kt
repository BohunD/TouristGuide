package com.db.apps

import android.app.Activity
import android.util.Log
import com.db.apps.domain.usecases.AddToDbUseCase
import com.db.apps.domain.usecases.LikePlaceUseCase
import com.db.apps.model.PlaceEntity
import com.db.apps.model.ResultAttraction
import com.db.apps.presentation.place.PlaceActivity

class PlaceListenerImpl(
    private val activity: Activity,
    private val likePlaceUseCase: LikePlaceUseCase
) : PlaceListener {


    /*override fun onLayoutClick( place: ResultAttraction) {
        val intent = PlaceActivity.newIntent(activity,place)
        activity.startActivity(intent)
    }*/

    override fun onLayoutClick( place: PlaceEntity) {
        val intent = PlaceActivity.newIntent(activity,place)
        activity.startActivity(intent)
    }

    override suspend fun onLikeClick(place: ResultAttraction) {
        //addToFavouritesUseCase.execute(place)
    }

    override suspend fun onLikeClick(place: PlaceEntity) {
        Log.d("OnLike", place.toString())
        likePlaceUseCase.execute(place)
    }

}