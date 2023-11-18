package com.db.apps.presentation.favourites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.db.apps.PlaceListenerImpl
import com.db.apps.data.repository.LocationRepositoryImpl
import com.db.apps.databinding.FragmentFavouritesBinding
import com.db.apps.domain.usecases.AddToFavouritesUseCase
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.presentation.favourites.FavouritesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var adapter: FavouritesAdapter
    private lateinit var listener: PlaceListenerImpl
    private lateinit var repository: LocationRepositoryImpl
    private lateinit var getFromFavouritesUseCase: GetFromFavouritesUseCase
    private lateinit var addToFavouritesUseCase: AddToFavouritesUseCase
    private lateinit var viewModel: FavouritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = LocationRepositoryImpl(requireContext())
        val vmFactory = FavouritesViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, vmFactory)[FavouritesViewModel::class.java]
        adapter = FavouritesAdapter()
        binding.rvAttractions.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            getPlaces()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("FavouritesFragment", "onResume")
        getPlaces()
    }

    private fun getPlaces() {
        Log.d("FavouritesFragment", "getPlaces")
        getFromFavouritesUseCase = GetFromFavouritesUseCase(repository)
        addToFavouritesUseCase = AddToFavouritesUseCase(repository)
        lifecycleScope.launch(Dispatchers.IO) {
            val placesFromDb = getFromFavouritesUseCase.execute()
            Log.d("FavouritesFragment", "places: $placesFromDb")
            withContext(Dispatchers.Main) {
                getFromFavouritesUseCase.execute().observe(viewLifecycleOwner) {
                    adapter.setList(it)
                    Log.e("MyLoggg", it.toString())
                    listener = PlaceListenerImpl(requireActivity(), addToFavouritesUseCase)
                    adapter.setClickListener(listener)

                }
            }
        }

    }


    companion object {

        @JvmStatic
        fun newInstance() =
            FavouritesFragment()
    }
}