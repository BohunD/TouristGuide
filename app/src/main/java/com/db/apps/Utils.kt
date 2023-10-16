package com.db.apps

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.db.apps.retrofit.MyGoogleApiService
import com.db.apps.retrofit.MyRetrofitClient


object Utils {
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"

    val googleApiService: MyGoogleApiService
        get() = MyRetrofitClient.getClient(GOOGLE_API_URL).create(MyGoogleApiService::class.java)
}

fun ImageView.loadUrl(context: Context, url: String?, errorDrawable: Int = R.drawable.no_image){
    val options = RequestOptions()
        .placeholder(progressDrawable(context))
        .error(errorDrawable)
    Glide.with(context).load(url).apply(options).into(this)
}
fun progressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
}
