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
import androidx.lifecycle.lifecycleScope
import com.db.apps.PlaceListenerImpl
import com.db.apps.SharedPrefsUtil
import com.db.apps.SingleLiveData
import com.db.apps.Utils
import com.db.apps.data.repository.LocationRepositoryImpl
import com.db.apps.databinding.FragmentAttractionsBinding
import com.db.apps.domain.usecases.AddToDbUseCase
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.domain.usecases.LikePlaceUseCase
import com.db.apps.getPhotoUrl
import com.db.apps.model.PlaceEntity
import com.db.apps.model.RootAttraction
import com.db.apps.presentation.favourites.FavouritesAdapter
import com.db.apps.retrofit.MyGoogleApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class AttractionsFragment : Fragment() {

    private lateinit var binding: FragmentAttractionsBinding
    private lateinit var myService: MyGoogleApiService

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private var myRvList = mutableListOf<PlaceEntity>()
    private lateinit var adapter: FavouritesAdapter

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var listener: PlaceListenerImpl
    private var cityNameLD = SingleLiveData<String>()

    private lateinit var repository: LocationRepositoryImpl
    private lateinit var addToFavouritesUseCase: AddToDbUseCase
    private lateinit var getFromFavouritesUseCase: GetFromFavouritesUseCase
    private lateinit var likePlaceUseCase: LikePlaceUseCase
    private var cityFromPrefs: String?=null
    private var sharedPrefsUtil:SharedPrefsUtil?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAttractionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefsUtil = SharedPrefsUtil(requireContext())
        myService = Utils.googleApiService
        repository = LocationRepositoryImpl(requireContext())
        addToFavouritesUseCase = AddToDbUseCase(repository)
        getFromFavouritesUseCase = GetFromFavouritesUseCase(repository)
        likePlaceUseCase = LikePlaceUseCase(repository)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.latLngLD.observe(requireActivity()) {
            Log.d("ADDING_PLACES", "sharedViewModel.latLngLD.observe")
            lat = it.latitude
            lng = it.longitude
            lifecycleScope.launch(Dispatchers.IO){
                cityFromPrefs= sharedPrefsUtil?.getString()
                getCityName(lat, lng)
            }
                showInfo()
        }
    }

    private fun showInfo(){
        cityNameLD.observe(viewLifecycleOwner){
            Log.d("ADDING_PLACES", "cityNameLD.observe")

            if(cityFromPrefs!=null){
                Log.d("cityFromPrefs!=null", "$cityFromPrefs")
                if(cityFromPrefs == it){
                    observePlaces()
                }else{
                    sharedPrefsUtil?.saveString(it)
                    findAttractions()

                }
            }
            binding.tvCityName.text = it
        }
    }

    private fun observePlaces(){
        Log.d("ADDING_PLACES", "observePlaces")

        lifecycleScope.launch(Dispatchers.IO) {
            val attractions = getFromFavouritesUseCase.execute()

            if(attractions.isEmpty()){
                Log.d("ADDING_PLACES", "attractions.isEmpty()")
                findAttractions()
            }else{
                Log.d("ADDING_PLACES", "else: ${attractions.toString()}")
                myRvList = attractions.toMutableList()
                withContext(Dispatchers.Main){
                    initAdapter()

                }

            }
        }


    }

    private fun findAttractions() {
        Log.d("ADDING_PLACES"," findAttractions()")
        val url = getUrl()
        myService.getPlacesOfInterest(url)
            .enqueue(object : retrofit2.Callback<RootAttraction> {

                override fun onResponse(
                    call: Call<RootAttraction>,
                    response: Response<RootAttraction>,
                ) {
                    try {
                        myRvList.clear()
                            if (response.isSuccessful && response.body()?.results?.isNotEmpty()!!) {
                            for (i in 0 until response.body()?.results?.size!!) {
                                val googlePlace = response.body()?.results!![i]
                                var photo = ""
                                if (googlePlace.photos.isNotEmpty()) {
                                    photo = getPhotoUrl(googlePlace.photos.get(0))
                                }
                                myRvList.add(
                                    PlaceEntity(
                                        googlePlace.placeId!!,
                                        googlePlace.name!!,
                                        googlePlace.rating!!,
                                        googlePlace.formattedAddress!!,
                                        googlePlace.geometry?.location?.lat!!,
                                        googlePlace.geometry?.location?.lng!!,
                                        photo
                                    )
                                )


                            }
                        }
                        addPlacesToDb()
                        initAdapter()
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "Internet error. Try again", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RootAttraction>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed :(", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun initAdapter(){
        adapter = FavouritesAdapter()
        adapter.setList(myRvList)
        binding.rvAttractions.adapter = adapter
        listener = PlaceListenerImpl(requireActivity(), likePlaceUseCase)
        adapter.setClickListener(listener)
    }

    private fun addPlacesToDb(){
        Log.d("ADDING_PLACES", "addPlacesToDb")
        lifecycleScope.launch(Dispatchers.IO){
            Log.d("ADDING_PLACES", myRvList.toString())
            for(place in myRvList){
                addToFavouritesUseCase.execute(place)
            }
        }

    }

    private fun getUrl(): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json")
        googlePlaceUrl.append(
            "?query=${cityNameLD.value}+city+point+of+interest" +
                    "&language=${Locale.getDefault()}" +
                    "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw"
        )
        Log.d("My_URL", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }



    private suspend fun getCityName(lat: Double, long: Double) {
        var name: String?
        try {
            withContext(Dispatchers.IO) {
                val geoCoder = Geocoder(requireContext(), Locale.US)
                val address = geoCoder.getFromLocation(lat, long, 1)
                name = address?.get(0)?.locality
                if (name == null) {
                    name = address?.get(0)?.adminArea
                    if (name == null) {
                        name = address?.get(0)?.subAdminArea
                    }
                }
                withContext(Dispatchers.Main){
                    cityNameLD.value = name!!
                }
            }
        } catch (e: IOException) {
            // Handle the exception (e.g., log it or show an error message)
            Log.e("GeocodingError", "Error getting city name", e)
        }
    }

    companion object {

        private const val LAT = "lat"
        private const val LNG = "lng"

        @JvmStatic
        fun newInstance() =
            AttractionsFragment()
    }
}