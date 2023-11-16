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
import com.db.apps.PlaceListenerImpl
import com.db.apps.RvAdapter
import com.db.apps.Utils
import com.db.apps.databinding.FragmentAttractionsBinding
import com.db.apps.getPhotoUrl
import com.db.apps.model.Photo
import com.db.apps.model.ResultAttraction
import com.db.apps.model.RootAttraction
import com.db.apps.model.RootPhotos
import com.db.apps.retrofit.MyGoogleApiService
import retrofit2.Call
import retrofit2.Response
import java.util.Locale


class AttractionsFragment : Fragment() {

    private lateinit var binding: FragmentAttractionsBinding
    private lateinit var myService: MyGoogleApiService

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private val myRvList = mutableListOf<ResultAttraction>()
    private lateinit var adapter: RvAdapter

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var listener: PlaceListenerImpl

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
        sharedViewModel.latLngLD.observe(requireActivity()) {
            lat = it.latitude
            lng = it.longitude
        }
        findAttractions()
    }


    private fun findAttractions() {
        val url = getUrl()
        myService.getPlacesOfInterest(url)
            .enqueue(object : retrofit2.Callback<RootAttraction> {

                override fun onResponse(
                    call: Call<RootAttraction>,
                    response: Response<RootAttraction>,
                ) {
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty()!!) {
                        for (i in 0 until response.body()?.results?.size!!) {
                            val googlePlace = response.body()?.results!![i]
                            myRvList.add(googlePlace)

                            Log.d("findAttractions", googlePlace.photos.toString())

                        }
                    }
                    adapter = RvAdapter(myRvList)
                    binding.rvAttractions.adapter = adapter
                    listener = PlaceListenerImpl(requireActivity(), adapter)
                    adapter.setListener(listener)
                }

                override fun onFailure(call: Call<RootAttraction>, t: Throwable) {
                    Log.d("ATTRACTIONS2:", t.message.toString())
                    Toast.makeText(requireContext(), "Failed :(", Toast.LENGTH_LONG).show()
                }

            })
    }



    private fun getUrl(): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json")
        googlePlaceUrl.append(
            "?query=${getCityName(lat, lng)}+city+point+of+interest" +
                    "&language=${Locale.getDefault()}" +
                    "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw"
        )
        Log.d("My_URL", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }



    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String?
        val geoCoder = Geocoder(requireContext(), Locale.US)
        val address = geoCoder.getFromLocation(lat, long, 1)
        cityName = address?.get(0)?.locality
        if (cityName == null) {
            cityName = address?.get(0)?.adminArea
            if (cityName == null) {
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