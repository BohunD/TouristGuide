package com.db.apps

import android.app.Activity
import com.db.apps.model.ResultAttraction
import com.db.apps.presentation.place.PlaceActivity

class PlaceListenerImpl(
    private val activity: Activity,
    val adapter: RvAdapter,
) : PlaceListener {
    override fun onLayoutClick(place: ResultAttraction) {
        val intent = PlaceActivity.newIntent(activity,place)
        activity.startActivity(intent)
    }

}