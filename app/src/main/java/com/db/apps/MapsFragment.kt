package com.db.apps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.db.apps.databinding.FragmentMapsBinding
import com.db.apps.model.Root
import com.db.apps.retrofit.MyGoogleApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.Locale


class MapsFragment : Fragment() {

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private lateinit var lastLocation: Location
    private var marker: Marker? = null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    private lateinit var callback: OnMapReadyCallback
    private val myLD = MutableLiveData<CheckerLD<String>>()
    private lateinit var binding: FragmentMapsBinding

    private var myMap: GoogleMap? = null


    private lateinit var myService: MyGoogleApiService
    internal var currentPlace: Root? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        myService = Utils.googleApiService
        binding.botNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.market -> findPlacesNearBy(arrayListOf("store", "market"))
                R.id.hospital -> findPlacesNearBy(arrayListOf("hospital"))
                R.id.food -> findPlacesNearBy(arrayListOf("cafe", "restaurant"))
                R.id.education -> findPlacesNearBy(arrayListOf("school"))

            }
            true
        }

    }

    private fun findPlacesNearBy(placeTypes: List<String>) {
        myMap?.clear()
        callback.onMapReady(myMap!!)
        for(placeType in placeTypes) {
            val url = getUrl(lat, lng, placeType)
            myService.getPlacesNearby(url)
                .enqueue(object : retrofit2.Callback<Root> {
                    override fun onResponse(call: Call<Root>, response: Response<Root>) {
                        currentPlace = response.body()
                        if (response.isSuccessful && response.body()?.results?.isNotEmpty()!!) {
                            for (i in 0 until response.body()?.results?.size!!) {
                                val markerOptions = MarkerOptions()
                                val googlePlace = response.body()?.results!![i]
                                val curLat = googlePlace.geometry!!.location?.lat
                                val curLng = googlePlace.geometry.location?.lng
                                val curPlaceName = googlePlace.name
                                val curLatLng = LatLng(curLat!!, curLng!!)
                                markerOptions.position(curLatLng)
                                markerOptions.title(curPlaceName)
                                when (placeType) {
                                    "restaurant" -> {
                                        markerOptions.icon(
                                            bitmapDescriptorFromVector(
                                                R.drawable.ic_food
                                            )
                                        )
                                    }

                                    "cafe" -> {
                                        markerOptions.icon(
                                            bitmapDescriptorFromVector(
                                                R.drawable.ic_food
                                            )
                                        )
                                    }

                                    "store" -> {
                                        markerOptions.icon(
                                            bitmapDescriptorFromVector(
                                                R.drawable.ic_shop
                                            )
                                        )
                                    }

                                    "hospital" -> {
                                        markerOptions.icon(
                                            bitmapDescriptorFromVector(
                                                R.drawable.ic_hospital
                                            )
                                        )
                                    }

                                    "school" -> {
                                        markerOptions.icon(
                                            bitmapDescriptorFromVector(
                                                R.drawable.ic_education
                                            )
                                        )
                                    }

                                    else -> markerOptions.icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN
                                        )
                                    )
                                }

                                markerOptions.snippet(i.toString())

                                myMap!!.addMarker(markerOptions)
                                myMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
                                myMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
                                callback.onMapReady(myMap!!)
                            }


                        }
                    }

                    override fun onFailure(call: Call<Root>, t: Throwable) {
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

    private fun bitmapDescriptorFromVector(
        @DrawableRes vectorResId: Int,
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getUrl(lat: Double, lng: Double, placeType: String): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json" )
        googlePlaceUrl.append(
                "?location=$lat,$lng" +
                "&radius=10000" +
                "&type=$placeType" +
                "&key=AIzaSyDHj5-TbBeDNWb5imOdLOFPbT4ZFXkHftw")
        Log.d("My_URL", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }



    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_CODE
                )
            }
        } else {
            buildRequest()
            buildLocationCallback()
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(requireContext(), "Permission denied(", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                lastLocation = p0.locations[p0.locations.size - 1]
                if (marker != null) {
                    marker!!.remove()
                }
                lat = lastLocation.latitude
                lng = lastLocation.longitude
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main, AttractionsFragment.newInstance(lat, lng))
                    .commit()
                initMapCallback()
            }
        }
    }

    private fun initMapCallback() {
        callback = OnMapReadyCallback { mMap ->
            val latLng = LatLng(lat, lng)
            val optionsMarker = MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            marker = mMap.addMarker(optionsMarker)

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }

            mMap.uiSettings.isZoomControlsEnabled = true
            lifecycleScope.launch(Dispatchers.Main) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
            }
            myMap = mMap
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    private fun buildRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

    }

    companion object {
        private const val PERMISSION_CODE = 101
        fun newInstance(): Fragment {
            return MapsFragment()
        }
    }
}