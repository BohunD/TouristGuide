package com.db.apps.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.db.apps.ViewPagerAdapter
import com.db.apps.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val pages = arrayOf(
        "Page 1",
        "page 2"
    )
    // TODO add Progressbar while geeting places nearby;
    // TODO add livedata for "location received" and then make tabslayout visible
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        binding.tabLayout.visibility = View.GONE
        viewModel.locationReceivedLD.observe(this){
            if(it.getContentIfNotHandled()==true){
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000)
                    binding.tabLayout.visibility = View.VISIBLE
                }
            }
        }
        val view = binding.root
        setContentView(view)
        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pages[position]
        }.attach()
    }
}