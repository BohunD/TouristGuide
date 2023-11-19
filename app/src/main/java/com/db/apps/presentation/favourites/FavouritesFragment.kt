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
import com.db.apps.domain.usecases.AddToDbUseCase
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.domain.usecases.GetLikedPlacesUseCase
import com.db.apps.domain.usecases.LikePlaceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var adapter: FavouritesAdapter
    private lateinit var listener: PlaceListenerImpl
    private lateinit var repository: LocationRepositoryImpl
    private lateinit var getLikePlaceUseCase: GetLikedPlacesUseCase
    private lateinit var getFromFavouritesUseCase: GetFromFavouritesUseCase
    private lateinit var likePlaceUseCase: LikePlaceUseCase
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
        //val vmFactory = FavouritesViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity())[FavouritesViewModel::class.java]
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
        getLikePlaceUseCase = GetLikedPlacesUseCase(repository)
        likePlaceUseCase = LikePlaceUseCase(repository)
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                getLikePlaceUseCase.execute().observe(viewLifecycleOwner) {
                    adapter.setList(it)
                    viewModel.updateList(it)
                    Log.e("MyLoggg", it.toString())
                    listener = PlaceListenerImpl(requireActivity(), likePlaceUseCase)
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