package com.db.apps.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.db.apps.R
import com.db.apps.bitmapDescriptorFromVector
import com.db.apps.databinding.FragmentPlacesNearbyBinding
import com.db.apps.model.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlacesNearbyFragment : Fragment() {

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var fromPlace = false

    private lateinit var lastLocation: Location
    private var marker: Marker? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var callback: OnMapReadyCallback
    private lateinit var binding: FragmentPlacesNearbyBinding
    private var localPlacesTypes = listOf<String>()

    private var myMap: GoogleMap? = null
    private lateinit var viewModel: MapsFragmentViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    checkPermission()
                    delay(1000)
                    binding.llGif.visibility = View.GONE
                    binding.botNavView.visibility = View.VISIBLE
                }
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    "Location permission not granted. Closing the application.",
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mLat = arguments?.getDouble(LAT)
        val mLng = arguments?.getDouble(LNG)
        if (mLat != null&& mLng!=null) {
            lat = mLat
            lng = mLng
            fromPlace = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlacesNearbyBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel = ViewModelProvider(
            this,
            MapsFragmentViewModelFactory(requireActivity())
        )[MapsFragmentViewModel::class.java]
        checkPermission()
        binding.botNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.market -> localPlacesTypes = (arrayListOf("store", "market"))
                R.id.hospital -> localPlacesTypes = (arrayListOf("hospital"))
                R.id.food -> localPlacesTypes = (arrayListOf("cafe", "restaurant"))
                R.id.education -> localPlacesTypes = (arrayListOf("school", "university"))
            }
            findPlacesNearBy(localPlacesTypes)
            true
        }
    }

    private fun findPlacesNearBy(placesTypes: List<String>) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPlacesNearby(lat, lng, placesTypes)
        }
        showLoading()
        viewModel.placesNearbyLiveData.observe(viewLifecycleOwner) {
            showPlaces(localPlacesTypes, it)
        }
    }

    private fun showPlaces(placeTypes: List<String>, places: List<Result>) {
        myMap?.clear()
        initMapCallback()
        callback.onMapReady(myMap!!)
        for (placeType in placeTypes) {
            for (googlePlace in places) {
                val markerOptions = MarkerOptions()
                val curLatLng = LatLng(
                    googlePlace.geometry!!.location?.lat!!,
                    googlePlace.geometry.location?.lng!!
                )
                markerOptions.position(curLatLng)
                markerOptions.title(googlePlace.name)
                when (placeType) {
                    "restaurant" -> {
                        markerOptions.icon(
                            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_food)
                        )
                    }

                    "cafe" -> {
                        markerOptions.icon(
                            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_food)
                        )
                    }

                    "store" -> {
                        markerOptions.icon(
                            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_shop)
                        )
                    }

                    "hospital" -> {
                        markerOptions.icon(
                            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_hospital)
                        )
                    }

                    "school" -> {
                        markerOptions.icon(
                            bitmapDescriptorFromVector(requireContext(), R.drawable.ic_education)
                        )
                    }

                    else -> return
                }
                myMap!!.addMarker(markerOptions)
                myMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
                myMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
                callback.onMapReady(myMap!!)
            }
            finishLoading()
        }
    }

    private fun showLoading() {
        binding.llProgress.visibility = View.VISIBLE
        binding.botNavView.visibility = View.GONE
    }

    private fun finishLoading() {
        binding.llProgress.visibility = View.GONE
        binding.botNavView.visibility = View.VISIBLE
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            buildRequest()
            buildLocationCallback(fromPlace)
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun buildLocationCallback(isPlace: Boolean) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                lastLocation = p0.locations[p0.locations.size - 1]
                if (marker != null) {
                    marker!!.remove()
                }
                if (!isPlace) {
                    lat = lastLocation.latitude
                    lng = lastLocation.longitude
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(800)
                    binding.llGif.visibility = View.GONE
                    binding.botNavView.visibility = View.VISIBLE
                }
                sharedViewModel.saveLatLng(LatLng(lat, lng))
                sharedViewModel.setLocationReceived(true)
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

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))

            myMap = mMap
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
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
        private const val LAT: String = "Lat"
        private const val LNG: String = "Lng"
        fun newInstance(myLat: Double?, myLng: Double?): Fragment {
            return PlacesNearbyFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LAT, myLat!!)
                    putDouble(LNG, myLng!!)
                }
            }
        }
    }
}