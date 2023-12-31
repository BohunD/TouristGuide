package com.db.apps

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.db.apps.presentation.AttractionsFragment
import com.db.apps.presentation.PlacesNearbyFragment
import com.db.apps.presentation.favourites.FavouritesFragment


private const val NUM_TABS = 3

public class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PlacesNearbyFragment()
            1 -> return AttractionsFragment()
            2 -> return FavouritesFragment()
        }
        return PlacesNearbyFragment()
    }
}