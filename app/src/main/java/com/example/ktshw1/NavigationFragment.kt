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


class NavigationFragment: Fragment(R.layout.fragment_navigation) {

    private val binding: FragmentNavigationBinding by viewBinding(FragmentNavigationBinding::bind)
    private val bindingFragment: FragmentFeedBinding by viewBinding( FragmentFeedBinding::bind )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(bindingFragment.feed)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}