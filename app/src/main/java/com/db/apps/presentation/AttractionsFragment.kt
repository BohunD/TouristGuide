package com.db.apps.presentation

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.db.apps.RvAdapter
import com.db.apps.Utils
import com.db.apps.databinding.FragmentAttractionsBinding
import com.db.apps.model.Photo
import com.db.apps.model.PlaceItemRv
import com.db.apps.model.RootAttraction
import com.db.apps.retrofit.MyGoogleApiService
import retrofit2.Call
import retrofit2.Response
import java.util.Locale


class AttractionsFragment : Fragment() {

    private lateinit var binding: FragmentAttractionsBinding
    private lateinit var myService: MyGoogleApiService

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private val myRvList = mutableListOf<PlaceItemRv>()
    private lateinit var adapter: RvAdapter

    private lateinit var  sharedViewModel: SharedViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.latLngLD.observe(requireActivity()){
            lat = it.latitude
            lng = it.longitude
        }
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
                            var photoUrl = ""
                            if(googlePlace.photos.size>0) {
                                 photoUrl = getPhotoUrl(googlePlace.photos[0])
                            }
                            val myPlace = PlaceItemRv(
                                photoUrl,
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
                    Log.d("ATTRACTIONS2:", t.message.toString())
                    Toast.makeText(requireContext(), "Failed :(", Toast.LENGTH_LONG).show()
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
        fun newInstance() =
            AttractionsFragment()
    }
}