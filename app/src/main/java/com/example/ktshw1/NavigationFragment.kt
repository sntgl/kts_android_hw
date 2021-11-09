package com.example.ktshw1

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.ktshw1.databinding.FragmentNavigationBinding
import com.example.ktshw1.databinding.FragmentProfileBinding
import androidx.navigation.ui.NavigationUI

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ktshw1.databinding.FragmentFeedBinding
import com.example.ktshw1.networking.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback


class NavigationFragment: Fragment(R.layout.fragment_navigation) {

    private val feedViewModel: FeedViewModel by viewModel()
    private val binding: FragmentNavigationBinding by viewBinding(FragmentNavigationBinding::bind)
    private val bindingFragment: FragmentFeedBinding by viewBinding( FragmentFeedBinding::bind )

    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val feedFragment = FeedFragment()
    private val profileFragment = ProfileFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewpager2
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.bottomNavigationFeed -> viewPager.currentItem = 0
                R.id.bottomNavigationProfile -> viewPager.currentItem = 1
            }
            return@setOnItemSelectedListener false
        }
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when (position) {
                    0 -> binding.bottomNavigation.menu.findItem(R.id.bottomNavigationFeed).isChecked =
                        true
                    1 -> binding.bottomNavigation.menu.findItem(R.id.bottomNavigationProfile).isChecked =
                        true
                }
            }
        })

        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        viewPagerAdapter?.addFragment(feedFragment)
        viewPagerAdapter?.addFragment(profileFragment)

        viewPager.adapter = viewPagerAdapter
    }
}