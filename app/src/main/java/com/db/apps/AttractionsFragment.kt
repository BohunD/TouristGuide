package com.db.apps

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.apps.databinding.FragmentAttractionsBinding
import com.db.apps.model.Photo
import com.db.apps.model.PlaceItemRv
import com.db.apps.model.Root
import com.db.apps.model.RootAttraction
import com.db.apps.retrofit.MyGoogleApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.Marker
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList
import java.util.Locale


class AttractionsFragment : Fragment() {

    private lateinit var binding: FragmentAttractionsBinding
    private lateinit var myService: MyGoogleApiService

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private val myRvList = mutableListOf<PlaceItemRv>()
    private lateinit var adapter: RvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            lat = it?.getDouble(LAT)!!
            lng = it.getDouble(LNG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAttractionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myService = Utils.googleApiService
        buildLocationCallback()
        findAttractions()

    }

    private fun buildLocationCallback() {

    }

    private fun findAttractions(){
        val url = getUrl()
        myService.getPlacesOfInterest(url)
            .enqueue(object : retrofit2.Callback<RootAttraction> {

                override fun onResponse(
                    call: Call<RootAttraction>,
                    response: Response<RootAttraction>,
                ) {
                    val currentPlace = response.body()
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty()!!) {
                        for (i in 0 until response.body()?.results?.size!!) {
                            val googlePlace = response.body()?.results!![i]
                            val myPlace = PlaceItemRv(
                                photoUrl = getPhotoUrl(googlePlace.photos[0]),
                                placeName = googlePlace.name.toString(),
                                placeDescription = googlePlace.formattedAddress.toString()
                            )
                            myRvList.add(myPlace)
                            //val curLat = googlePlace.geometry!!.location?.lat
                            //val curLng = googlePlace.geometry!!.location?.lng

                            val curPlaceName = googlePlace.name
                            Log.d("ATTRACTIONS:", curPlaceName.toString())
                        }
                    }
                    adapter = RvAdapter(myRvList)
                    binding.rvAttractions.adapter = adapter
                }

                override fun onFailure(call: Call<RootAttraction>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun getUrl(): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json" )
        googlePlaceUrl.append(
            "?query=${getCityName(lat,lng)}+city+point+of+interest" +
                    "&language=${Locale.getDefault()}"+
                    "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw")
        Log.d("My_URL", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun getPhotoUrl(photo: Photo?) :String {
        if (photo != null) {
            return StringBuilder(
                "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=${photo.width}" +
                        "&photo_reference=${photo.photo_reference}" +
                        "&key=AIzaSyDED3IlT2SjXseJPPeHWd5kGS1KJDCEsGQ"
            ).toString()
        }else return ""
    }


    private fun getCityName(lat: Double,long: Double):String{
        var cityName: String?
        val geoCoder = Geocoder(requireContext(), Locale.US)
        val address = geoCoder.getFromLocation(lat,long,1)
        cityName = address?.get(0)?.locality
        if (cityName == null){
            cityName = address?.get(0)?.adminArea
            if (cityName == null){
                cityName = address?.get(0)?.subAdminArea
            }
        }
        return cityName!!
    }

    companion object {

        private const val LAT = "lat"
        private const val LNG = "lng"
        @JvmStatic
        fun newInstance(lat: Double, lng: Double) =
            AttractionsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LAT, lat)
                    putDouble(LNG, lng)
                }
            }
    }
}