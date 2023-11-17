package com.db.apps.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.db.apps.PlaceListenerImpl
import com.db.apps.data.repository.LocationRepositoryImpl
import com.db.apps.databinding.FragmentFavouritesBinding
import com.db.apps.domain.usecases.AddToFavouritesUseCase
import com.db.apps.domain.usecases.GetFromFavouritesUseCase
import com.db.apps.model.ResultAttraction
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = LocationRepositoryImpl(requireContext())
        getFromFavouritesUseCase = GetFromFavouritesUseCase(repository)
        addToFavouritesUseCase = AddToFavouritesUseCase(repository)
        lifecycleScope.launch(Dispatchers.IO) {
            val placesFromDb = getFromFavouritesUseCase.execute()
            Log.d("FavouritesFragment", "places: $placesFromDb")
            adapter = FavouritesAdapter(placesFromDb)
            listener = PlaceListenerImpl(requireActivity(), addToFavouritesUseCase)
            adapter.setClickListener(listener)
            withContext(Dispatchers.Main){
                binding.rvAttractions.adapter = adapter
            }
        }


    }



    companion object {

        @JvmStatic
        fun newInstance() =
            FavouritesFragment()
    }
}