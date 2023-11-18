package com.db.apps.presentation.place

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.db.apps.GetPhotosFromApi
import com.db.apps.R
import com.db.apps.databinding.ActivityPlaceBinding
import com.db.apps.model.Photo
import com.db.apps.model.PlaceEntity
import com.db.apps.model.ResultAttraction
import com.db.apps.presentation.PlacesNearbyFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceBinding
    private var currentPlace: PlaceEntity?=null
    private var adapter: PhotosRvAdapter?=null
    private var photos: ArrayList<Photo>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(PLACE)){
            currentPlace = intent.getParcelableExtra(PLACE)
        }
        binding.ratingBar.rating = currentPlace?.rating?.toFloat()!!
        binding.tvPlaceName.text = currentPlace?.place_name
        binding.tvAddress.text = currentPlace?.formattedAddress
        getPhotos()

        val placeLat = currentPlace?.placeLat
        val placeLng = currentPlace?.placeLng
        binding.ivMap.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlacesNearbyFragment.newInstance(placeLat, placeLng))
                .addToBackStack("PlaceOnMap")
                .commit()
        }

    }


    private fun getPhotos(){
        val getPhotos = GetPhotosFromApi(currentPlace!!)
        getPhotos.getPhotos()
        getPhotos.photosLD.observe(this){
            setPhotos(it)
        }
    }

    private fun setPhotos(list: ArrayList<Photo>){
        Log.d("setPhotos", list.toString())
        adapter = PhotosRvAdapter(list)
        binding.rvPhotos.adapter = adapter
    }

    companion object {

        private const val PLACE = "place"
        private const val PLACE_ENTITY = "place"
        @JvmStatic
        fun newIntent(context: Context,place: PlaceEntity): Intent{
            val intent = Intent(context, PlaceActivity::class.java)
            intent.putExtra(PLACE_ENTITY, place)
            return intent
        }

    }
}